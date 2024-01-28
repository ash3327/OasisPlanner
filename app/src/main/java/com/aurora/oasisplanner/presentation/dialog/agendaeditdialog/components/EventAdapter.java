package com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components;

import android.annotation.SuppressLint;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.model.entities.util._Doc;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.data.util.Switch;
import com.aurora.oasisplanner.databinding.ItemAlarmBinding;
import com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.AlarmEditDialog;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class EventAdapter extends _BaseAdapter<EventAdapter.EventHolder, _Event> {

    private int len;
    private final AlarmEditDialog.OnSaveListener onSaveAlarmListener;
    public Switch bSwitch = new Switch(false);

    private Activity activity;
    private Agenda agenda;

    public EventAdapter(AlarmEditDialog.OnSaveListener onSaveAlarmListener,
                        OnSelectListener onSelectListener,
                        RecyclerView recyclerView, Switch tSwitch, Agenda agenda) {
        super(onSelectListener, tSwitch);
        this.onSaveAlarmListener = onSaveAlarmListener;
        tSwitch.observe((state)->{
            if (!state) checkedList.clear();
        }, true);
        this.agenda = agenda;
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding = null;
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        assert ActivityType.Type.values()[viewType].equals(ActivityType.Type.activity);
        binding = ItemAlarmBinding.inflate(li, parent, false);
        return new EventHolder(binding, this, len, tSwitch, checkedList);
    }

    @Override
    public void onBindViewHolder(@NonNull EventHolder holder, int position) {
        holder.bind(position, items.get(holder.getAdapterPosition()), activity);
    }

    /** Since the alarm list will be overall changed when any agenda is edited,
     *  a global notification in change of ui is required. */
    @SuppressLint("NotifyDataSetChanged")
    public void setEventList(Activity activity) {
        _setEventList(activity);
        notifyDataSetChanged();
    }
    private void _setEventList(Activity activity) {
        this.activity = activity;
        List<Object> list = new ArrayList<>();
        List<ActivityType.Type> types = new ArrayList<>();

        int i = 0;
        List[] objlist = activity.getObjList(true);
        for (Object obj : objlist[0]) {
            list.add(obj);
            types.add((ActivityType.Type) objlist[1].get(i));
            i++;
        }

        this.items = list;
        this.types = types;
        this.len = i;
    }

    public void removeChecked() {
        try {
            for (_Event aL : checkedList)
                remove(aL, aL.i);
            tSwitch.setState(false);
        } catch (Exception e) {e.printStackTrace();}
    }

    public void editTagOfChecked() {
        AppModule.retrieveEditEventUseCases().invokeDialogForTagType(
                checkedList, this::updateUi
        );
    }

    public void moveChecked() {
        AppModule.retrieveEditEventUseCases().invokeDialogForMovingEvent(
                checkedList, agenda, activity, this::forceUpdateUi
        );
    }

    public void remove(Object obj, int i) {
        if (i == -1) return;
        ActivityType type = activity.activity.types.get(i);
        boolean valid = true;
        if (obj instanceof _Event && type.type == ActivityType.Type.activity
                && AppModule.retrieveAgendaUseCases().getAlarmLists(activity).get(type.i).equals(obj))
            ((_Event) obj).visible = false;
        else if (obj instanceof _Doc && type.type == ActivityType.Type.doc && activity.docs.get(type.i).equals(obj))
            ((_Doc) obj).visible = false;
        else if (obj instanceof _Doc && type.type == ActivityType.Type.loc && activity.docs.get(type.i).equals(obj))
            ((_Doc) obj).visible = false;
        else valid = false;
        if (valid) {
            activity.activity.types.remove(i);
            activity.update();
            setEventList(activity);
        }
    }

    /** the index i is the index i IN THE VISUAL LIST. */
    public void insert(ActivityType.Type type, int i, String s) {
        switch (type) {
            case activity:
                _Event gp = _Event.empty();
                gp.setTitle(s);
                gp.putArgs(TagType.DESCR.name(), new SpannableStringBuilder(s));
                activity.activity.types.add(i, new ActivityType(type,
                        AppModule.retrieveAgendaUseCases().getAlarmLists(activity).size()));
                AppModule.retrieveAgendaUseCases().getAlarmLists(activity).add(gp);
                break;
            case doc:
                _Doc doc = _Doc.empty();
                activity.activity.types.add(i, new ActivityType(type, activity.docs.size()));
                activity.docs.add(doc);
                break;
            case loc:
                _Doc loc = _Doc.empty();
                activity.activity.types.add(i, new ActivityType(ActivityType.Type.loc, activity.docs.size()));
                activity.docs.add(loc);
                break;
        }

        activity.update();
        setEventList(activity);
    }

    List<_Event> getList() { return activity.alarmLists; }

    public void refreshDataset() { setEventList(activity); }

    private boolean swapping = false;
    public void swapItems(int fromPosition, int toPosition) {
        if (swapping) return;
        try {
            swapping = true;
            Collections.swap(items, fromPosition, toPosition);
            super.notifyItemMoved(fromPosition, toPosition);
            swapping = false;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void save() {
        int i = 0;
        for (Object aL : items) {
            if (!(aL instanceof _Event))
                continue;
            ((_Event)aL).i = i;
            i++;
        }
        activity.getObjList(true);
    }

    class EventHolder extends _BaseHolder<EventAdapter.EventHolder, _Event, EventAdapter> {
        private Instant clicked = Instant.now();
        private Object item;
        private final int len;

        public EventHolder(ViewDataBinding binding, EventAdapter adapter, int len,
                           Switch tSwitch, Set<_Event> checkedList) {
            super(binding, adapter, tSwitch, checkedList);
            this.len = len;
        }

        public boolean bind(int i, Object sect, Activity gp) {
            this.item = sect;
            if (sect instanceof _Event)
                return bindAlarms(i, (_Event) sect, gp);
            return false;
        }

        public boolean bindAlarms(int i, _Event gp, Activity grp) {
            ItemAlarmBinding binding = (ItemAlarmBinding) vbinding;

            setDragHandle(binding.itemAlarmDraghandle, this);

            aSwitch.observe((state)-> {
                binding.itemAlarmCheckbox.setChecked(checkedList.contains(gp));
                binding.itemAlarmCheckbox.setVisibility(state ? View.VISIBLE : View.GONE);
                binding.itemAlarmDraghandle.setVisibility(!state ? View.VISIBLE : View.GONE);
                binding.itemAlarmCheckbox.setOnCheckedChangeListener((v,checked)->{
                    try {
                        if (checked) checkedList.add(gp);
                        else checkedList.remove(gp);
                        adapter.onUpdate(checkedList);
                        aSwitch.setState(!checkedList.isEmpty());
                    } catch (Exception e){e.printStackTrace();}
                });
            }, true, gp.getUniqueReference());

            binding.bar.setOnClickListener(
                    (v)->{
                        if (Instant.now().toEpochMilli() - clicked.toEpochMilli() < 500)
                            return;
                        if (aSwitch.getState())
                            checkToggle(gp);
                        else {
                            AppModule.retrieveEditEventUseCases()
                                    .invoke(
                                            gp, grp,
                                            onSaveAlarmListener
                                    );
                        }
                    }
            );
            binding.bar.setOnLongClickListener(
                    (v)->{
                        checkToggle(gp);

                        clicked = Instant.now();
                        return true;
                    }
            );

            alarmRefreshUi();

            return true;
        }

        public void alarmRefreshUi() {
            ItemAlarmBinding binding = (ItemAlarmBinding) vbinding;
            if (item instanceof _Event) {
                _Event gp = (_Event) item;

                binding.icon.setImageDrawable(gp.type.getDrawable());
                binding.importanceLabel.setColorFilter(gp.importance.getColorPr());
                binding.barDescriptionText.setText(gp.getDateTime());
                RecyclerView recyclerView = binding.itemAlarmRecyclerView;
                TagsAdapter adapter = new TagsAdapter();
                recyclerView.setLayoutManager(new LinearLayoutManager(vbinding.getRoot().getContext()));
                recyclerView.setAdapter(adapter);
                recyclerView.suppressLayout(true); // prevent it from having any kind of interaction
                adapter.setTags(gp.getArgs());
            }
        }
    }
}

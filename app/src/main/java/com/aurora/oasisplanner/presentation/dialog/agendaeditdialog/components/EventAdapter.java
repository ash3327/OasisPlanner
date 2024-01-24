package com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.model.entities.events._AlarmList;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.model.entities.util._Doc;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.data.util.Id;
import com.aurora.oasisplanner.data.util.Switch;
import com.aurora.oasisplanner.databinding.ItemAlarmBinding;
import com.aurora.oasisplanner.databinding.ItemDocBinding;
import com.aurora.oasisplanner.databinding.ItemGapBinding;
import com.aurora.oasisplanner.databinding.ItemLocBinding;
import com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.AlarmEditDialog;
import com.aurora.oasisplanner.util.styling.Resources;
import com.aurora.oasisplanner.util.styling.Styles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {

    private static final int ID_KEY_ITEMS = 3;
    private final Id id;
    private int len;
    private final AlarmEditDialog.OnSaveListener onSaveAlarmListener;
    private final OnSelectListener onSelectListener;
    public Switch bSwitch = new Switch(false);

    {
        setHasStableIds(true);
        id = new Id(-1, ID_KEY_ITEMS);
    }

    private Activity activity;
    public List<Object> sections = new ArrayList<>();
    public List<ActivityType.Type> types = new ArrayList<>();

    private final Switch tSwitch;
    private final Set<_AlarmList> checkedList;

    public EventAdapter(AlarmEditDialog.OnSaveListener onSaveAlarmListener,
                        OnSelectListener onSelectListener,
                        RecyclerView recyclerView, Switch tSwitch) {
        this.onSaveAlarmListener = onSaveAlarmListener;
        this.onSelectListener = onSelectListener;
        this.tSwitch = tSwitch;
        checkedList = new HashSet<>();
        id.setId(-1);
        tSwitch.observe((state)->{
            if (!state) checkedList.clear();
        }, true);
        id.observe((oldId, newId)->{
            if(oldId >= 0) notifyItemChanged(oldId);
            if((oldId < 0 || newId % 2 == 0) && newId >= 0) notifyItemChanged(newId);
            recyclerView.requestLayout();
        });//*/
    }

    @Override
    public int getItemViewType(int position) {
        return types.get(position).ordinal();
    }

    @Override
    public long getItemId(int position) {
        Object item = sections.get(position);
        long numClasses = 3;
        if (item instanceof _AlarmList)
            return Styles.hashInt(item) * numClasses + 1;
        return -1;
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
        holder.bind(position, sections.get(position), activity);
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    /** Since the alarm list will be overall changed when any agenda is edited,
     *  a global notification in change of ui is required. */
    @SuppressLint("NotifyDataSetChanged")
    public void setEventList(Activity activity) {
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

        this.sections = list;
        this.types = types;
        this.len = i;
        notifyDataSetChanged();
    }

    public void removeChecked() {
        try {
            for (_AlarmList aL : checkedList)
                remove(aL, aL.i);
            tSwitch.setState(false);
        } catch (Exception e) {e.printStackTrace();}
    }

    public void editTagOfChecked() {
        AppModule.retrieveEditAlarmListUseCases().invokeDialogForTagType(
                checkedList, this::updateUi
        );
    }

    public void updateUi() {
        checkedList.clear();
        tSwitch.setState(false, true);
        notifyDataSetChanged();
    }

    public boolean checkListIsEmpty() {
        return checkedList.isEmpty();
    }

    public void remove(Object obj, int i) {
        if (i == -1) return;
        ActivityType type = activity.activity.types.get(i);
        boolean valid = true;
        if (obj instanceof _AlarmList && type.type == ActivityType.Type.activity
                && AppModule.retrieveAgendaUseCases().getAlarmLists(activity).get(type.i).equals(obj))
            ((_AlarmList) obj).visible = false;
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
                _AlarmList gp = _AlarmList.empty();
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

        id.setId(i * 2 + 1);
        activity.update();
        setEventList(activity);
    }

    public interface OnSelectListener {void onSelect(Set<_AlarmList> checkedList, boolean isFull);}
    public void onUpdate(Set<_AlarmList> checkedList) {
        if (onSelectListener != null)
            onSelectListener.onSelect(checkedList, checkedList.size()==activity.alarmLists.size());
    }
    public void clearChecked() {
        checkedList.clear();
        tSwitch.setState(false, true);
    }
    public void checkAll() {
        checkedList.addAll(activity.alarmLists);
        tSwitch.setState(true, true);
    }

    class EventHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding vbinding;
        private final EventAdapter adapter;
        /** aSwtich = true shows the checkboxes. */
        private final Switch aSwitch;
        private final Set<_AlarmList> checkedList;
        private Instant clicked = Instant.now();
        private Object item;
        private final int len;

        public EventHolder(ViewDataBinding binding, EventAdapter adapter, int len,
                           Switch tSwitch, Set<_AlarmList> checkedList) {
            super(binding.getRoot());
            this.vbinding = binding;
            this.adapter = adapter;
            this.len = len;
            this.aSwitch = tSwitch;
            this.checkedList = checkedList;
        }

        public boolean bind(int i, Object sect, Activity gp) {
            this.item = sect;
            if (sect instanceof _AlarmList)
                return bindAlarms(i, (_AlarmList) sect, gp);
            return false;
        }

        public boolean bindAlarms(int i, _AlarmList gp, Activity grp) {
            ItemAlarmBinding binding = (ItemAlarmBinding) vbinding;

            aSwitch.observe((state)-> {
                binding.itemAlarmCheckbox.setChecked(checkedList.contains(gp));
                binding.itemAlarmCheckbox.setVisibility(state ? View.VISIBLE : View.GONE);
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
                        if (id.equals(i)) {
                            if (Instant.now().toEpochMilli() - clicked.toEpochMilli() < 500)
                                return;
                            if (aSwitch.getState())
                                checkToggle(gp);
                            else {
                                AppModule.retrieveEditAlarmListUseCases()
                                        .invoke(
                                                gp, grp,
                                                onSaveAlarmListener
                                        );
                            }
                        }
                        else if (aSwitch.getState())
                            checkToggle(gp);
                        else {
                            if (id.setId(i) && Instant.now().toEpochMilli() - clicked.toEpochMilli() > 500) {
                                aSwitch.setState(false);
                                adapter.onUpdate(checkedList);
                            }

                            AppModule.retrieveEditAlarmListUseCases()
                                    .invoke(
                                            gp, grp,
                                            onSaveAlarmListener
                                    );
                        }
                    }
            );
            binding.bar.setOnLongClickListener(
                    (v)->{
                        id.setId(i);
                        checkToggle(gp);

                        clicked = Instant.now();
                        return true;
                    }
            );

            alarmRefreshUi();

            return true;
        }
        public void checkToggle(_AlarmList gp) {
            if (checkedList.contains(gp))
                checkedList.remove(gp);
            else
                checkedList.add(gp);
            aSwitch.setState(!checkedList.isEmpty(), true);
            adapter.onUpdate(checkedList);
        }
        public void alarmRefreshUi() {
            ItemAlarmBinding binding = (ItemAlarmBinding) vbinding;
            if (item instanceof _AlarmList) {
                _AlarmList gp = (_AlarmList) item;

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

        public void associate(EditText editText, _Doc doc) {
            //prevents triggering at the initial change in text (initialization)
            Object tag = editText.getTag();
            if (tag instanceof TextWatcher)
                editText.removeTextChangedListener((TextWatcher) tag);

            editText.setText(doc.contents);
        }
    }
}

package com.aurora.oasisplanner.presentation.dialogs.agendaeditdialog.components;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.model.entities.util._Doc;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.model.pojo.events.Alarm;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.presentation.dialogs.agendaeditdialog.util.AgendaAccessUtil;
import com.aurora.oasisplanner.presentation.util.Switch;
import com.aurora.oasisplanner.databinding.ItemAlarmBinding;
import com.aurora.oasisplanner.presentation.dialogs.alarmeditdialog.AlarmEditDialog;
import com.aurora.oasisplanner.util.styling.Resources;
import com.aurora.oasisplanner.util.styling.Styles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class EventAdapter extends _BaseAdapter<EventAdapter.EventHolder, _Event> {

    private int len;
    private final AlarmEditDialog.OnSaveListener onSaveAlarmListener;
    private final OnPinnedChangedListener onPinnedChangedListener;
    public Switch bSwitch = new Switch(false);

    private Activity activity;
    private Agenda agenda;

    private int mPinned;

    public EventAdapter(AlarmEditDialog.OnSaveListener onSaveAlarmListener,
                        OnSelectListener onSelectListener, OnPinnedChangedListener onPinnedChangedListener,
                        RecyclerView recyclerView, Switch tSwitch, Agenda agenda,
                        long eventLId, int pinned) {
        super(onSelectListener, recyclerView, tSwitch, eventLId);
        this.onSaveAlarmListener = onSaveAlarmListener;
        this.onPinnedChangedListener = onPinnedChangedListener;
        tSwitch.observe((state)->{
            if (!state) checkedList.clear();
        }, true);
        this.agenda = agenda;
        mPinned = pinned;
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

        List[] objlist = activity.getObjList(true);
        List<Object> list = new ArrayList<Object>(objlist[0]);
        List<ActivityType.Type> types = new ArrayList<>((List<ActivityType.Type>) objlist[1]);
        int _mPinned = AgendaAccessUtil.fetchEventId(list, LId);
        if (mPinned == AgendaAccessUtil.NIL_VAL)
            mPinned = _mPinned;

        this.items = list;
        this.types = types;
        this.len = list.size();

        if (mPinned != AgendaAccessUtil.NIL_VAL)
            recyclerView.scrollToPosition(mPinned);
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
        if (i == AgendaAccessUtil.NIL_VAL) return;
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
    public static void _insert(Activity activity, ActivityType.Type type, int i, String s, _Event gp) {
        switch (type) {
            case activity:
                if (gp == null) {
                    gp = _Event.empty();
                    gp.setTitle(s);
                    gp.putArgs(TagType.DESCR.name(), new SpannableStringBuilder(s));
                }
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
    }
    /** the index i is the index i IN THE VISUAL LIST. */
    public void insert(ActivityType.Type type, int i, String s) {
        _insert(activity, type, i, s, null);
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

    public void setPinned(int i) {
        mPinned = i;
        if (onPinnedChangedListener != null)
            onPinnedChangedListener.onPinChanged(i);
    }
    public _Event getPinnedEvent() {
        if (mPinned == AgendaAccessUtil.NIL_VAL || mPinned >= items.size())
            return null;
        _Event pinnedEvent = (_Event) items.get(mPinned);
        pinnedEvent.activityDescr = getContentString(pinnedEvent);
        return pinnedEvent;
    }
    public String getContentString(_Event event) {
        return Alarm.getContentStringFrom(
                agenda.getTitle(),
                activity.getTitle(),
                event.getTitle(),
                false
        ).toString();
    }

    class EventHolder extends _BaseHolder<EventAdapter.EventHolder, _Event, EventAdapter> {
        private Instant clicked = Instant.now();
        private Object item;
        private final int len;
        private int i = AgendaAccessUtil.NIL_VAL;

        public EventHolder(ViewDataBinding binding, EventAdapter adapter, int len,
                           Switch tSwitch, Set<_Event> checkedList) {
            super(binding, adapter, tSwitch, checkedList);
            this.len = len;
        }

        public boolean bind(int i, Object sect, Activity gp) {
            this.i = i;
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
                        barOnClicked(i, gp, grp.activity.title);
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

            bindDocText(binding.docTag, binding.docTagTv, gp, i, grp.activity.title);

            return true;
        }

        public void alarmRefreshUi() {
            ItemAlarmBinding binding = (ItemAlarmBinding) vbinding;
            if (item instanceof _Event) {
                _Event gp = (_Event) item;

                // Highlight current selected event
                binding.marker.setVisibility(adapter.mPinned == this.i ? View.VISIBLE : View.GONE);

                // Icon of Alarm
                Drawable icon = gp.type.getOutlineDrawable();
                StringBuilder eachIsDone = new StringBuilder();
                if (gp.type == AlarmType.todo) {
                    boolean allDone = true;
                    for (_Alarm alarm : ((_Event) item).getAssociates().alarms) {
                        boolean isDone = alarm.isFinished();
                        eachIsDone.append(isDone ? "T" : "F");
                        allDone &= isDone;
                    }
                    if (allDone)
                        icon = Resources.getDrawable(R.drawable.ic_assignment_outline_done);
                }
                icon.setColorFilter(
                        gp.importance.getColorPr(),
                        PorterDuff.Mode.SRC_IN
                );
                binding.icon.setImageDrawable(icon);

                // Details of Alarm
                SpannableStringBuilder desc = new SpannableStringBuilder(gp.getDateTime());
                SpannableStringBuilder loc = gp.getLoc();
                if (loc != null) desc.append(" • "+loc);

                String to_append = " • ";
                if (gp.getArg(TagType.ALARM) != null) {
                    Styles.appendImageSpan(
                            binding.barDescriptionText, desc.append(to_append), R.drawable.menuic_notification
                    );
                    to_append = " ";
                }
                if (!Styles.isEmpty(gp.getTagsString())) {
                    Styles.appendImageSpan(
                            binding.barDescriptionText, desc.append(to_append), R.drawable.ic_tag
                    );
                    to_append = " ";
                }
                binding.barDescriptionText.setText(desc);

                // Event expanded list of arguments
                RecyclerView recyclerView = binding.itemAlarmRecyclerView;

                if (!expanded) {
                    recyclerView.suppressLayout(false);
                    recyclerView.setVisibility(View.GONE);
                    return;
                }
                recyclerView.setVisibility(View.VISIBLE);
                TagsAdapter adapter = new TagsAdapter();
                recyclerView.setLayoutManager(new LinearLayoutManager(vbinding.getRoot().getContext()));
                recyclerView.setAdapter(adapter);
                recyclerView.suppressLayout(true); // prevent it from having any kind of interaction
                adapter.setTags(gp.getArgs(), Arrays.asList(TagType.DESCR, TagType.LOC));
            }
        }

        public void barOnClicked(int i, _Event gp, SpannableStringBuilder activityTitle) {
            if (Instant.now().toEpochMilli() - clicked.toEpochMilli() < 500)
                return;
            if (aSwitch.getState())
                checkToggle(gp);
            else {
                adapter.setPinned(i);
                AppModule.retrieveEditEventUseCases().invoke(gp, getContentString(gp), onSaveAlarmListener, null);
            }
        }
    }

    public interface OnPinnedChangedListener {void onPinChanged(int mPinned);}
}

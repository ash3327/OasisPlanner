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
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.model.pojo.events.Alarm;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.presentation.util.Id;
import com.aurora.oasisplanner.presentation.util.Switch;
import com.aurora.oasisplanner.databinding.SectionBinding;
import com.aurora.oasisplanner.databinding.SectionDocBinding;
import com.aurora.oasisplanner.databinding.SectionGapBinding;
import com.aurora.oasisplanner.util.styling.DateTimesFormatter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

public class ActivityAdapter extends _BaseAdapter<ActivityAdapter.ActivityHolder, _Activity> {

    private static final int ID_KEY_SECTIONS_ADD = 4;
    private Id toAddSection = new Id(0, ID_KEY_SECTIONS_ADD); //4
    private OnClickListener ocl = null;
    private Instant clicked = Instant.now();

    private Agenda agenda;

    public ActivityAdapter(OnSelectListener onSelectListener, RecyclerView recyclerView,
                           Switch tSwitch, long activityLId) {
        super(onSelectListener, recyclerView, tSwitch, activityLId);
    }

    @NonNull
    @Override
    public ActivityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding = null;
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        switch (ActivityType.Type.values()[viewType]) {
            case gap:
                binding = SectionGapBinding.inflate(li, parent, false);
                break;
            case activity:
                binding = SectionBinding.inflate(li, parent, false);
                break;
            case doc:
                binding = SectionDocBinding.inflate(li, parent, false);
                break;
        }
        return new ActivityHolder(binding, this, tSwitch, checkedList);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityHolder holder, int position) {
        holder.bind(position, items.get(holder.getAdapterPosition()));
    }

    /** Since the alarm list will be overall changed when any agenda is edited,
     *  a global notification in change of ui is required. */

    @SuppressLint("NotifyDataSetChanged")
    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
        List<Object> list = new ArrayList<>();
        List<ActivityType.Type> types = new ArrayList<>();

        int i = 0, pinned = -1;
        List[] objlist = agenda.getObjList(true);
        for (Object obj : objlist[0]) {
            list.add(obj);
            if (obj instanceof _Activity && ((_Activity)obj).id == LId)
                pinned = i;
            types.add((ActivityType.Type) objlist[1].get(i));
            i++;
        }

        this.items = list;
        this.types = types;
        notifyDataSetChanged();
        if (pinned != -1)
            recyclerView.scrollToPosition(pinned);
    }

    public void remove(Object obj, int i) {
        if (obj instanceof _Activity)
            ((_Activity) obj).visible = false;
        toAddSection.setId(0);
        agenda.agenda.types.remove(i);
        agenda.update();
        setAgenda(agenda);
    }

    public interface OnClickListener {void onClick(_Activity activity);}
    public void setOnClickListener(OnClickListener ocl) {
        this.ocl = ocl;
    }

    /** the index i is the index i IN THE VISUAL LIST. */
    public static void _insert(Agenda agenda, ActivityType.Type type, int i, String descr) {
        switch (type) {
            case activity:
                _Activity gp = new _Activity(descr);
                gp.setCache(Activity.empty());
                List<_Activity> activities = AppModule.retrieveAgendaUseCases().getActivities(agenda);
                agenda.agenda.types.add(i, new ActivityType(type, activities.size()));
                activities.add(gp);
                break;
        }
        agenda.update();
    }

    /** the index i is the index i IN THE VISUAL LIST. */
    public void insert(ActivityType.Type type, int i, String descr) {
        _insert(agenda, type, i, descr);
        toAddSection.setId(0);
        setAgenda(agenda);
    }

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
            if (!(aL instanceof _Activity))
                continue;
            ((_Activity)aL).i = i;
            i++;
        }
        agenda.getObjList(true);
    }

    public void removeChecked() {
        try {
            for (_Activity aL : checkedList)
                remove(aL, aL.i);
            tSwitch.setState(false);
        } catch (Exception e) {e.printStackTrace();}
    }
    public void editTagOfChecked() {
        /* TODO: Edit tag for activity
        AppModule.retrieveEditEventUseCases().invokeDialogForTagType(
                checkedList, this::updateUi
        );//*/
    }
    public void moveChecked() {}

    List<_Activity> getList() { return agenda.activities; }

    public void refreshDataset() { setAgenda(agenda); }

    class ActivityHolder extends _BaseHolder<ActivityAdapter.ActivityHolder, _Activity, ActivityAdapter> {

        public ActivityHolder(ViewDataBinding binding, ActivityAdapter adapter, Switch tSwitch, Set<_Activity> checkedList) {
            super(binding, adapter, tSwitch, checkedList);
        }

        public boolean bind(int i, Object sect) {
            if (sect instanceof _Activity)
                return bindActivity(i, (_Activity) sect);
            return false;
        }

        public boolean bindActivity(int i, _Activity gp) {
            SectionBinding binding = (SectionBinding) vbinding;
            Switch tSwitch = new Switch(false);

            setDragHandle(binding.activityDraghandle, this);

            aSwitch.observe((state)-> {
                binding.activityCheckbox.setChecked(checkedList.contains(gp));
                binding.activityCheckbox.setVisibility(state ? View.VISIBLE : View.GONE);
                binding.activityDraghandle.setVisibility(!state ? View.VISIBLE : View.GONE);
                binding.activityCheckbox.setOnCheckedChangeListener((v,checked)->{
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
                        barOnClicked(i, gp, null);
                        tSwitch.setState(false);
                    }
            );
            binding.bar.setOnLongClickListener(
                    (v)->{
                        checkToggle(gp);

                        clicked = Instant.now();
                        return true;
                    }
            );
            boolean expanded = false;
            int visibility = expanded ? View.VISIBLE : View.GONE;
            int antiVisibility = !expanded ? View.VISIBLE : View.GONE;
            //binding.sectionItems.setVisibility(visibility);
            binding.sectionDetails.setVisibility(antiVisibility);//*/

            ExecutorService executor = AppModule.provideExecutor();
            executor.submit(()->{
                try {
                    Alarm nextAlarm = AppModule.retrieveAgendaUseCases().getNextAlarm(gp);
                    binding.getRoot().post(()->{
                        if (nextAlarm != null) {
                            binding.sectdI1.setImageDrawable(nextAlarm.getType().getDrawable());
                            //binding.importanceLabel.setColorFilter(gpl.alarmList.importance.getColorPr());
                            binding.sectdT1.setText(DateTimesFormatter.getDateTime(nextAlarm.getDateTime()));
                        } else {
                            binding.sectdI1.setVisibility(View.GONE);
                            binding.sectdT1.setVisibility(View.GONE);
                        }
                        SpannableStringBuilder ssb = null;
                        if (nextAlarm != null)
                            ssb = nextAlarm.getLoc();
                        if (nextAlarm != null && ssb != null) {
                            binding.sectdI2.setImageDrawable(TagType.LOC.getDrawable());
                            binding.sectdT2.setText(ssb);
                        } else {
                            binding.sectdI2.setVisibility(View.GONE);
                            binding.sectdT2.setVisibility(View.GONE);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Drawable icon = gp.getType().getDrawable();
            icon.setColorFilter(
                    gp.getImportance().getColorPr(),
                    PorterDuff.Mode.SRC_IN
            );
            binding.docIcon.setImageDrawable(icon);

            bindDocText(binding.docTag, binding.docTagTv, gp, i, null);

            return true;
        }

        public void barOnClicked(int i, _Activity gp, SpannableStringBuilder ssb) {
            if (Instant.now().toEpochMilli() - clicked.toEpochMilli() < 500)
                return;
            if (aSwitch.getState())
                checkToggle(gp);
            else {
                toAddSection.setId(0);
                if (ocl != null)
                    ocl.onClick(gp);
            }
        }
    }
}

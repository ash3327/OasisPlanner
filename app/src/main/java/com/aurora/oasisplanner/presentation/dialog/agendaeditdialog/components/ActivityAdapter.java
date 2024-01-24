package com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.entities.events._AlarmList;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.model.entities.util._Doc;
import com.aurora.oasisplanner.data.model.pojo.events.Alarm;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.data.util.Id;
import com.aurora.oasisplanner.data.util.Switch;
import com.aurora.oasisplanner.databinding.SectionBinding;
import com.aurora.oasisplanner.databinding.SectionDocBinding;
import com.aurora.oasisplanner.databinding.SectionGapBinding;
import com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.AlarmEditDialog;
import com.aurora.oasisplanner.util.styling.DateTimesFormatter;
import com.aurora.oasisplanner.util.styling.Resources;
import com.aurora.oasisplanner.util.styling.Styles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityHolder> {

    private static final int ID_KEY_SECTIONS = 2, ID_KEY_SECTIONS_ADD = 4;
    private Id toAddSection = new Id(0, ID_KEY_SECTIONS_ADD);
    private Id.IdObj scrollFunc = (oi, i)->{};
    private OnClickListener ocl = null;
    private Instant clicked = Instant.now();

    {
        setHasStableIds(true);
    }

    private Agenda agenda;
    public List<Object> activities = new ArrayList<>();
    public List<ActivityType.Type> types = new ArrayList<>();

    private final OnSelectListener onSelectListener;
    private final Switch tSwitch;
    private final Set<_Activity> checkedList;

    public ActivityAdapter(OnSelectListener onSelectListener, Switch tSwitch) {
        this.onSelectListener = onSelectListener;
        this.tSwitch = tSwitch;
        this.checkedList = new HashSet<>();
    }

    @Override
    public int getItemViewType(int position) {
        return types.get(position).ordinal();
    }

    @Override
    public long getItemId(int position) {
        Object item = activities.get(position);
        long numClasses = 3;
        if (item instanceof _Activity)
            return Styles.hashInt(item) * numClasses + 1;
        return -1;
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
        holder.bind(position, activities.get(position));
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    /** Since the alarm list will be overall changed when any agenda is edited,
     *  a global notification in change of ui is required. */

    public void setAgenda(Agenda agenda) {
        setAgenda(agenda, -2);
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setAgenda(Agenda agenda, long activityLId) {
        this.agenda = agenda;
        List<Object> list = new ArrayList<>();
        List<ActivityType.Type> types = new ArrayList<>();

        int i = 0;
        List[] objlist = agenda.getObjList(true);
        for (Object obj : objlist[0]) {
            list.add(obj);
            types.add((ActivityType.Type) objlist[1].get(i));
            i++;
        }

        this.activities = list;
        this.types = types;
        notifyDataSetChanged();
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
    public void insert(ActivityType.Type type, int i, String descr) {
        switch (type) {
            case activity:
                _Activity gp = new _Activity(descr);
                gp.setCache(Activity.empty());
                List<_Activity> activities = AppModule.retrieveAgendaUseCases().getActivities(agenda);
                agenda.agenda.types.add(i, new ActivityType(type, activities.size()));
                activities.add(gp);
                break;
        }
        toAddSection.setId(0);
        agenda.update();
        setAgenda(agenda);
        //id.setId(i * 2 + 1);
    }

    public void setScrollToFunc(Id.IdObj scrollFunc) {
        this.scrollFunc = scrollFunc;
    }

    class ActivityHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding vbinding;
        private ActivityAdapter adapter;
        private final Set<_Activity> checkedList;
        /** aSwtich = true shows the checkboxes. */
        private final Switch aSwitch;

        public ActivityHolder(ViewDataBinding binding, ActivityAdapter adapter, Switch tSwitch, Set<_Activity> checkedList) {
            super(binding.getRoot());
            this.vbinding = binding;
            this.adapter = adapter;
            this.aSwitch = tSwitch;
            this.checkedList = checkedList;
        }

        public boolean bind(int i, Object sect) {
            if (sect instanceof _Activity)
                return bindActivity(i, (_Activity) sect);
            return false;
        }

        public boolean bindActivity(int i, _Activity gp) {
            SectionBinding binding = (SectionBinding) vbinding;
            Switch tSwitch = new Switch(false);

            aSwitch.observe((state)-> {
                binding.activityCheckbox.setChecked(checkedList.contains(gp));
                binding.activityCheckbox.setVisibility(state ? View.VISIBLE : View.GONE);
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
                        barOnClicked(i, gp);
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
            //*
            binding.btnDelete.setOnClickListener(
                    (v)->adapter.remove(gp, i/2)
            );
            boolean expanded = false;
            int visibility = expanded ? View.VISIBLE : View.GONE;
            int antiVisibility = !expanded ? View.VISIBLE : View.GONE;
            binding.btnDelete.setVisibility(visibility);
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

            Drawable bg = binding.sectionCard.getBackground();
            bg.setColorFilter(
                    gp.getImportance().getColorPr(),
                    PorterDuff.Mode.SRC_IN
            );//*/
            binding.sectionCard.setBackground(bg);

            EditText docText = binding.docTag;
            associate(docText, gp);
            docText.setFocusableInTouchMode(true);
            docText.setFocusable(true);
            docText.setOnClickListener(
                    (v)->{
                        docText.setFocusable(true);
                        barOnClicked(i, gp);
                    }
            );//*/

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    docText.clearComposingText();
                    gp.descr = new SpannableStringBuilder(docText.getText());
                }
            };
            docText.setTag(textWatcher);
            docText.addTextChangedListener(textWatcher);

            return true;
        }

        public void barOnClicked(int i, _Activity gp) {
            if (Instant.now().toEpochMilli() - clicked.toEpochMilli() < 500)
                return;
            if (aSwitch.getState())
                checkToggle(gp);
            else {
                toAddSection.setId(0);
                scrollFunc.run(i, i);
                if (ocl != null)
                    ocl.onClick(gp);
            }
        }

        public void associate(EditText editText, _Activity activity) {
            //prevents triggering at the initial change in text (initialization)
            Object tag = editText.getTag();
            if (tag instanceof TextWatcher)
                editText.removeTextChangedListener((TextWatcher) tag);

            editText.setText(activity.descr);
        }

        public void associate(EditText editText, _Doc doc) {
            //prevents triggering at the initial change in text (initialization)
            Object tag = editText.getTag();
            if (tag instanceof TextWatcher)
                editText.removeTextChangedListener((TextWatcher) tag);

            editText.setText(doc.contents);
        }

        public void checkToggle(_Activity gp) {
            if (checkedList.contains(gp))
                checkedList.remove(gp);
            else
                checkedList.add(gp);
            aSwitch.setState(!checkedList.isEmpty(), true);
            adapter.onUpdate(checkedList);
        }
    }

    private void updateLabel(Label lbl, Label lbl2, boolean collapsed, boolean checkListIsOn,
                             View.OnClickListener removeChecked, View.OnClickListener editTagofChecked,
                             Function<Boolean, Boolean> checkListIsEmpty, Switch bSwitch) {
        if (checkListIsEmpty == null) checkListIsEmpty = (v)->false;
        if (lbl == null) return;
        lbl.vg.setOnClickListener(
                collapsed ?
                    this::addNewSection :
                        checkListIsOn ?
                            editTagofChecked :
                            this::refreshCollapsed);
        lbl.tv.setText(collapsed ?
                Resources.getString(R.string.yellow_bar_text_newevent) :
                    checkListIsOn ?
                        Resources.getString(R.string.yellow_bar_text_tag):
                        Resources.getString(R.string.yellow_bar_text_collapse));
        lbl.imgv.setImageResource(collapsed ?
                R.drawable.ic_symb_plus :
                    checkListIsOn ?
                        R.drawable.ic_edit_label :
                        R.drawable.ic_contract);

        if (lbl2 == null) return;
        boolean listEmpty = checkListIsEmpty.apply(true);
        if (!collapsed) {
            if (checkListIsOn) {
                lbl2.vg.setVisibility(View.VISIBLE);
                lbl2.tv.setText(Resources.getString(
                        listEmpty ? R.string.yellow_bar_text_collapse : R.string.yellow_bar_text_delete
                ));
                lbl2.imgv.setImageResource(listEmpty ? R.drawable.ic_contract : R.drawable.ic_trashcan);
                lbl2.vg.setOnClickListener(removeChecked);
            } else if (bSwitch != null) {
                lbl2.tv.setText(Resources.getString(
                         bSwitch.getState() ? R.string.yellow_bar_text_collapse : R.string.yellow_bar_text_newevent
                ));
                lbl2.imgv.setImageResource(bSwitch.getState() ? R.drawable.ic_contract : R.drawable.ic_symb_plus);
                lbl2.vg.setVisibility(View.VISIBLE);
                lbl2.vg.setOnClickListener((v)->{
                    boolean state = bSwitch.toggleState();
                    lbl2.tv.setText(Resources.getString(
                            state ? R.string.yellow_bar_text_collapse : R.string.yellow_bar_text_newevent
                    ));
                    lbl2.imgv.setImageResource(state ? R.drawable.ic_contract : R.drawable.ic_symb_plus);
                });
            } else {
                lbl2.vg.setVisibility(View.GONE);
            }
        } else {
            lbl2.vg.setVisibility(View.GONE);
        }
    }

    public void refreshCollapsed(View v) {
        toAddSection.setId(0);
    }
    public void addNewSection(View v) {
        toAddSection.setId(1);
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
        AppModule.retrieveEditAlarmListUseCases().invokeDialogForTagType(
                checkedList, this::updateUi
        );//*/
    }

    public void clearChecked() {
        checkedList.clear();
        tSwitch.setState(false, true);
    }
    public void checkAll() {
        checkedList.addAll(agenda.activities);
        tSwitch.setState(true, true);
    }

    public void updateUi() {
        checkedList.clear();
        tSwitch.setState(false, true);
        notifyDataSetChanged();
    }

    public interface OnSelectListener {void onSelect(Set<_Activity> checkedList, boolean isFull);}
    public void onUpdate(Set<_Activity> checkedList) {
        if (onSelectListener != null)
            onSelectListener.onSelect(checkedList, checkedList.size()==agenda.activities.size());
    }

    public static class Label {
        ViewGroup vg;
        TextView tv;
        ImageView imgv;

        public Label(ViewGroup vg, TextView tv, ImageView imgv) {
            this.vg = vg;
            this.tv = tv;
            this.imgv = imgv;
        }
    }
}

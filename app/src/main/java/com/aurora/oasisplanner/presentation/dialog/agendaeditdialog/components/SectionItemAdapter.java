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
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.model.entities._Alarm;
import com.aurora.oasisplanner.data.model.pojo.Activity;
import com.aurora.oasisplanner.data.model.pojo.AlarmList;
import com.aurora.oasisplanner.data.model.entities._Doc;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.data.core.AppModule;
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

public class SectionItemAdapter extends RecyclerView.Adapter<SectionItemAdapter.AlarmGroupsHolder> {

    private static final int ID_KEY_ITEMS = 3;
    private final Id id, parentId;
    private final int pid;
    private int len;
    private final AlarmEditDialog.OnSaveListener onSaveAlarmListener;

    {
        setHasStableIds(true);
        id = new Id(-1, ID_KEY_ITEMS);
    }

    private Activity activity;
    public List<Object> sections = new ArrayList<>();
    public List<ActivityType.Type> types = new ArrayList<>();

    private final Switch tSwitch;
    private final Set<AlarmList> checkedList;

    public SectionItemAdapter(AlarmEditDialog.OnSaveListener onSaveAlarmListener, RecyclerView recyclerView, Id parentId, int pid, Switch tSwitch) {
        this.onSaveAlarmListener = onSaveAlarmListener;
        this.parentId = parentId;
        this.pid = pid;
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
        if (item instanceof GapData)
            return ((GapData) item).i * numClasses;
        if (item instanceof AlarmList)
            return Styles.hashInt(item) * numClasses + 1;
        if (item instanceof _Doc)
            return Styles.hashInt(item) * numClasses + 2;
        return -1;
    }

    @NonNull
    @Override
    public AlarmGroupsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding = null;
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        switch (ActivityType.Type.values()[viewType]) {
            case gap:
                binding = ItemGapBinding.inflate(li, parent, false);
                break;
            case activity:
                binding = ItemAlarmBinding.inflate(li, parent, false);
                break;
            case doc:
                binding = ItemDocBinding.inflate(li, parent, false);
                break;
            case loc:
                binding = ItemLocBinding.inflate(li, parent, false);
                break;
        }
        return new AlarmGroupsHolder(binding, this, len, tSwitch, checkedList);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmGroupsHolder holder, int position) {
        holder.bind(position, sections.get(position), activity);
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    /** Since the alarm list will be overall changed when any agenda is edited,
     *  a global notification in change of ui is required. */
    @SuppressLint("NotifyDataSetChanged")
    public void setGroup(Activity activity) {
        this.activity = activity;
        List<Object> list = new ArrayList<>();
        List<ActivityType.Type> types = new ArrayList<>();

        int i = 0;
        List[] objlist = activity.getObjList(true);
        for (Object obj : objlist[0]) {
            list.add(new GapData(i));
            types.add(ActivityType.Type.gap);
            list.add(obj);
            types.add((ActivityType.Type) objlist[1].get(i));
            i++;
        }
        list.add(new GapData(i));
        types.add(ActivityType.Type.gap);

        this.sections = list;
        this.types = types;
        this.len = i;
        notifyDataSetChanged();
    }

    public void removeChecked() {
        try {
            for (AlarmList aL : checkedList)
                remove(aL, aL.alarmList.i);
            tSwitch.setState(false);
        } catch (Exception e) {e.printStackTrace();}
    }

    public void editTagOfChecked() {
        // TODO
        // create dialog to ask for change
        // picked loc -> confirm: do following:
        try {
            for (AlarmList aL : checkedList)
                aL.alarmList.putArgs(_Alarm.LOC, null);
            tSwitch.setState(false);
        } catch (Exception e) {e.printStackTrace();}
    }

    public boolean checkListIsEmpty(boolean v) {
        return checkedList.isEmpty();
    }

    public void remove(Object obj, int i) {
        if (i == -1) return;
        ActivityType type = activity.activity.types.get(i);
        boolean valid = true;
        if (obj instanceof AlarmList && type.type == ActivityType.Type.activity && activity.alarmList.get(type.i).equals(obj))
            ((AlarmList) obj).visible = false;
        else if (obj instanceof _Doc && type.type == ActivityType.Type.doc && activity.docs.get(type.i).equals(obj))
            ((_Doc) obj).visible = false;
        else if (obj instanceof _Doc && type.type == ActivityType.Type.loc && activity.docs.get(type.i).equals(obj))
            ((_Doc) obj).visible = false;
        else valid = false;
        if (valid) {
            activity.activity.types.remove(i);
            activity.update();
            setGroup(activity);
        }
    }

    /** the index i is the index i IN THE VISUAL LIST. */
    public void insert(ActivityType.Type type, int i) {
        switch (type) {
            case activity:
                AlarmList gp = AlarmList.empty();
                activity.activity.types.add(i, new ActivityType(type, activity.alarmList.size()));
                activity.alarmList.add(gp);
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
        setGroup(activity);
    }

    class AlarmGroupsHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding vbinding;
        private final SectionItemAdapter adapter;
        /** aSwtich = true shows the checkboxes. */
        private final Switch aSwitch;
        private final Set<AlarmList> checkedList;
        private Instant clicked = Instant.now();
        private Object item;
        private final int len;

        public AlarmGroupsHolder(ViewDataBinding binding, SectionItemAdapter adapter, int len,
                                 Switch tSwitch, Set<AlarmList> checkedList) {
            super(binding.getRoot());
            this.vbinding = binding;
            this.adapter = adapter;
            this.len = len;
            this.aSwitch = tSwitch;
            this.checkedList = checkedList;
        }

        public boolean bind(int i, Object sect, Activity gp) {
            this.item = sect;
            if (sect instanceof GapData)
                return bindGap(i, (GapData) sect);
            if (sect instanceof AlarmList)
                return bindAlarms(i, (AlarmList) sect, gp);
            if (sect instanceof _Doc) {
                _Doc doc = (_Doc) sect;
                if (getItemViewType() == ActivityType.Type.loc.ordinal())
                    return bindLoc(i, doc);
                return bindDoc(i, doc);
            }
            return false;
        }

        public boolean bindGap(int i, GapData gap) {
            ItemGapBinding binding = (ItemGapBinding) vbinding;

            binding.bar.setOnClickListener(
                    (v)->id.setId(parentId.setId(pid) ? -1 : i)
            );
            binding.bar.setFocusable(id.equals(i) && parentId.equals(pid));
            binding.tab.setVisibility(parentId.equals(pid) ? View.VISIBLE : View.GONE);
            binding.expandedTab.setVisibility(id.equals(i) ? View.VISIBLE : View.GONE);
            binding.collapsedTab.setVisibility(id.equals(i) ? View.GONE : View.VISIBLE);
            binding.veryCollapsedTab.setVisibility(parentId.equals(pid) || gap.i == 0 || gap.i == len ? View.GONE : View.VISIBLE);
            binding.btnAddGroup.setOnClickListener(
                    (v)->{
                        if (id.equals(i))
                            adapter.insert(ActivityType.Type.activity, gap.i);
                    }
            );
            binding.btnAddDoc.setOnClickListener(
                    (v)->{
                        if (id.equals(i))
                            adapter.insert(ActivityType.Type.doc, gap.i);
                    }
            );
            binding.btnAddLoc.setOnClickListener(
                    (v)->{
                        if (id.equals(i))
                            adapter.insert(ActivityType.Type.loc, gap.i);
                    }
            );

            return true;
        }

        public boolean bindAlarms(int i, AlarmList gp, Activity grp) {
            ItemAlarmBinding binding = (ItemAlarmBinding) vbinding;

            aSwitch.observe((state)-> {
                /*/
                binding.tab.setBackgroundColor(
                        id.equals(i) && parentId.equals(pid) && state ?
                                Resources.getColor(R.color.red_100) : 0
                );
                binding.collapsedTab.setVisibility(id.equals(i) && parentId.equals(pid) && state ? View.VISIBLE : View.GONE);
                //*/
                binding.itemAlarmCheckbox.setChecked(checkedList.contains(gp));
                binding.itemAlarmCheckbox.setVisibility(state ? View.VISIBLE : View.GONE);
                binding.itemAlarmCheckbox.setOnCheckedChangeListener((v,checked)->{
                    try {
                        if (checked) checkedList.add(gp);
                        else checkedList.remove(gp);
                    } catch (Exception e){e.printStackTrace();}
                });
            }, true);

            binding.bar.setOnClickListener(
                    (v)->{
                        if (id.equals(i) && parentId.equals(pid)) {
                            if (Instant.now().toEpochMilli() - clicked.toEpochMilli() < 500)
                                return;
                            if (aSwitch.getState())
                                checkToggle(gp);
                            else {
                                AppModule.retrieveAgendaUseCases().editAlarmListUseCase
                                        .invoke(
                                                gp, grp,
                                                onSaveAlarmListener
                                        );
                            }
                        }
                        else if (aSwitch.getState())
                            checkToggle(gp);
                        else {
                            parentId.setId(pid);
                            if (id.setId(i) && Instant.now().toEpochMilli() - clicked.toEpochMilli() > 500)
                                aSwitch.setState(false);

                            AppModule.retrieveAgendaUseCases().editAlarmListUseCase
                                    .invoke(
                                            gp, grp,
                                            onSaveAlarmListener
                                    );
                        }
                    }
            );
            binding.bar.setOnLongClickListener(
                    (v)->{
                        if (!(parentId.setId(pid) & id.setId(i))) {
                            checkedList.add(gp);
                            aSwitch.setState(true);
                            binding.itemAlarmCheckbox.setChecked(true);
                        }
                        clicked = Instant.now();
                        return false;
                    }
            );

            alarmRefreshUi();

            return true;
        }
        public void checkToggle(AlarmList gp) {
            if (checkedList.contains(gp))
                checkedList.remove(gp);
            else
                checkedList.add(gp);
            aSwitch.setState(true, true);
        }
        public void alarmRefreshUi() {
            ItemAlarmBinding binding = (ItemAlarmBinding) vbinding;
            if (item instanceof AlarmList) {
                AlarmList gp = (AlarmList) item;

                binding.icon.setImageDrawable(gp.alarmList.type.getDrawable());
                binding.importanceLabel.setColorFilter(gp.alarmList.importance.getColorPr());
                binding.barDescriptionText.setText(gp.alarmList.getDateTime());
            }
        }

        public boolean bindDoc(int i, _Doc doc) {
            ItemDocBinding binding = (ItemDocBinding) vbinding;

            aSwitch.observe((state)-> {
                binding.bar.setBackgroundColor(
                        id.equals(i) && parentId.equals(pid) && state ?
                                Resources.getColor(R.color.red_100) : 0
                );
                binding.card.setVisibility(id.equals(i) && parentId.equals(pid) && state ? View.VISIBLE : View.GONE);
                binding.docContentEdittext.setFocusable(parentId.equals(pid) && !state);
            }, true);

            binding.bar.setOnClickListener(
                    (v)->{
                        if (id.equals(i) && parentId.equals(pid)) {
                            if (aSwitch.getState() && (Instant.now().toEpochMilli() - clicked.toEpochMilli() > 500))
                                adapter.remove(doc, i/2);
                        } else {
                            parentId.setId(pid);
                            id.setId(i);
                            if (Instant.now().toEpochMilli() - clicked.toEpochMilli() > 500)
                                aSwitch.setState(false);
                        }
                    }
            );
            binding.docContentEdittext.setOnClickListener(
                    (v)->{
                        if (id.equals(i) && parentId.equals(pid)) {
                            if (aSwitch.getState() && (Instant.now().toEpochMilli() - clicked.toEpochMilli() > 500))
                                adapter.remove(doc, i/2);
                        } else {
                            parentId.setId(pid);
                            id.setId(i);
                        }
                    }
            );
            binding.bar.setOnLongClickListener(
                    (v)->{
                        if (!(parentId.setId(pid) & id.setId(i)))
                            aSwitch.setState(true);
                        clicked = Instant.now();
                        return false;
                    }
            );
            binding.docContentEdittext.setOnLongClickListener(
                    (v)->{
                        parentId.setId(pid);
                        id.setId(i);
                        aSwitch.setState(true);
                        clicked = Instant.now();
                        return false;
                    }
            );

            EditText editText;
            associate(editText = binding.docContentEdittext, doc);

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    editText.clearComposingText();
                    doc.contents = new SpannableStringBuilder(editText.getText());
                    //types.set(i/2, ActivityType.Type.loc);
                    //notifyItemChanged(i);
                }
            };
            editText.setTag(textWatcher);
            editText.addTextChangedListener(textWatcher);

            return true;
        }

        public boolean bindLoc(int i, _Doc doc) {
            ItemLocBinding binding = (ItemLocBinding) vbinding;

            aSwitch.observe((state)-> {
                binding.bar.setBackgroundColor(
                        id.equals(i) && parentId.equals(pid) && state ?
                                Resources.getColor(R.color.red_100) : 0
                );
                binding.card.setVisibility(id.equals(i) && parentId.equals(pid) && state ? View.VISIBLE : View.GONE);
                binding.docContentEdittext.setFocusable(parentId.equals(pid) && !state);
            }, true);

            binding.bar.setOnClickListener(
                    (v)->{
                        if (id.equals(i) && parentId.equals(pid)) {
                            if (aSwitch.getState() && (Instant.now().toEpochMilli() - clicked.toEpochMilli() > 500))
                                adapter.remove(doc, i/2);
                        } else {
                            parentId.setId(pid);
                            id.setId(i);
                            if (Instant.now().toEpochMilli() - clicked.toEpochMilli() > 500)
                                aSwitch.setState(false);
                        }
                    }
            );
            binding.docContentEdittext.setOnClickListener(
                    (v)->{
                        if (id.equals(i) && parentId.equals(pid)) {
                            if (aSwitch.getState() && (Instant.now().toEpochMilli() - clicked.toEpochMilli() > 500))
                                adapter.remove(doc, i/2);
                        } else {
                            parentId.setId(pid);
                            id.setId(i);
                        }
                    }
            );
            binding.bar.setOnLongClickListener(
                    (v)->{
                        if (!(parentId.setId(pid) & id.setId(i)))
                            aSwitch.setState(true);
                        clicked = Instant.now();
                        return false;
                    }
            );
            binding.docContentEdittext.setOnLongClickListener(
                    (v)->{
                        parentId.setId(pid);
                        id.setId(i);
                        aSwitch.setState(true);
                        clicked = Instant.now();
                        return false;
                    }
            );

            EditText editText;
            associate(editText = binding.docContentEdittext, doc);

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    editText.clearComposingText();
                    doc.contents = new SpannableStringBuilder(editText.getText());
                }
            };
            editText.setTag(textWatcher);
            editText.addTextChangedListener(textWatcher);

            return true;
        }

        public void associate(EditText editText, _Doc doc) {
            //prevents triggering at the initial change in text (initialization)
            Object tag = editText.getTag();
            if (tag instanceof TextWatcher)
                editText.removeTextChangedListener((TextWatcher) tag);

            editText.setText(doc.contents);
        }
    }

    public static class GapData {
        public int i;

        public GapData(int i) {
            this.i = i;
        }
    }
}

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
import com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.AlarmEditDialog;
import com.aurora.oasisplanner.util.styling.Resources;
import com.aurora.oasisplanner.util.styling.Styles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SectionItemAdapter extends RecyclerView.Adapter<SectionItemAdapter.AlarmGroupsHolder> {

    private static final int ID_KEY_ITEMS = 3;
    private Id id, parentId;
    private int pid;
    private int len;
    private AlarmEditDialog.OnSaveListener onSaveAlarmListener;

    {
        setHasStableIds(true);
        id = new Id(-1, ID_KEY_ITEMS);
    }

    private Activity activity;
    public List<Object> sections = new ArrayList<>();
    public List<ActivityType.Type> types = new ArrayList<>();

    public SectionItemAdapter(AlarmEditDialog.OnSaveListener onSaveAlarmListener, RecyclerView recyclerView, Id parentId, int pid) {
        this.onSaveAlarmListener = onSaveAlarmListener;
        this.parentId = parentId;
        this.pid = pid;
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
        }
        return new AlarmGroupsHolder(binding, this, len);
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

    public void remove(Object obj, int i) {
        ActivityType type = activity.activity.types.get(i);
        boolean valid = true;
        if (obj instanceof AlarmList && type.type == ActivityType.Type.activity && activity.alarmList.get(type.i).equals(obj))
            ((AlarmList) obj).visible = false;
        else if (obj instanceof _Doc && type.type == ActivityType.Type.doc && activity.docs.get(type.i).equals(obj))
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
        }
        id.setId(i * 2 + 1);
        activity.update();
        setGroup(activity);
    }

    class AlarmGroupsHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding vbinding;
        private SectionItemAdapter adapter;
        private Switch aSwitch = new Switch(false);
        private Instant clicked = Instant.now();
        private Object item;
        private int len;

        public AlarmGroupsHolder(ViewDataBinding binding, SectionItemAdapter adapter, int len) {
            super(binding.getRoot());
            this.vbinding = binding;
            this.adapter = adapter;
            this.len = len;
        }

        public boolean bind(int i, Object sect, Activity gp) {
            this.item = sect;
            aSwitch.setState(false);
            if (sect instanceof GapData)
                return bindGap(i, (GapData) sect);
            if (sect instanceof AlarmList)
                return bindAlarms(i, (AlarmList) sect, gp);
            if (sect instanceof _Doc)
                return bindDoc(i, (_Doc) sect);
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
            binding.btnAddDoc.setOnClickListener(
                    (v)->{
                        if (id.equals(i))
                            adapter.insert(ActivityType.Type.doc, gap.i);
                    }
            );
            binding.btnAddGroup.setOnClickListener(
                    (v)->{
                        if (id.equals(i))
                            adapter.insert(ActivityType.Type.activity, gap.i);
                    }
            );

            return true;
        }

        public boolean bindAlarms(int i, AlarmList gp, Activity grp) {
            ItemAlarmBinding binding = (ItemAlarmBinding) vbinding;

            aSwitch.observe((state)-> {
                binding.tab.setBackgroundColor(
                        id.equals(i) && parentId.equals(pid) && state ?
                                Resources.getColor(R.color.red_100) : 0
                );
                binding.collapsedTab.setVisibility(id.equals(i) && parentId.equals(pid) && state ? View.VISIBLE : View.GONE);
            }, true);

            binding.bar.setOnClickListener(
                    (v)->{
                        if (id.equals(i) && parentId.equals(pid)) {
                            if (Instant.now().toEpochMilli() - clicked.toEpochMilli() < 500)
                                return;
                            if (aSwitch.getState())
                                adapter.remove(gp, i/2);
                            else {
                                AppModule.retrieveAgendaUseCases().editAlarmListUseCase
                                        .invoke(
                                                gp, grp,
                                                onSaveAlarmListener
                                        );
                            }
                        } else {
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
                        if (!(parentId.setId(pid) & id.setId(i)))
                            aSwitch.setState(true);
                        clicked = Instant.now();
                        return false;
                    }
            );

            alarmRefreshUi();

            return true;
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

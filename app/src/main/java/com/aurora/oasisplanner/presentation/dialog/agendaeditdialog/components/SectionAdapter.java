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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.model.entities.util._Doc;
import com.aurora.oasisplanner.data.model.pojo.events.AlarmList;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.data.util.Id;
import com.aurora.oasisplanner.data.util.Switch;
import com.aurora.oasisplanner.databinding.SectionBinding;
import com.aurora.oasisplanner.databinding.SectionDocBinding;
import com.aurora.oasisplanner.databinding.SectionGapBinding;
import com.aurora.oasisplanner.util.styling.DateTimesFormatter;
import com.aurora.oasisplanner.util.styling.Resources;
import com.aurora.oasisplanner.util.styling.Styles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.AlarmGroupsHolder> {

    private static final int ID_KEY_SECTIONS = 2, ID_KEY_SECTIONS_ADD = 4;
    private Id id;
    private Id toAddSection = new Id(0, ID_KEY_SECTIONS_ADD);
    private Id.IdObj scrollFunc = (oi, i)->{};
    private Label label, label2;

    {
        setHasStableIds(true);
        id = new Id(-1, ID_KEY_SECTIONS);
        id.observe((oldId, newId)->{
            if(oldId >= 0) notifyItemChanged(oldId);
            if(newId >= 0) notifyItemChanged(newId);
        });
    }

    private Agenda agenda;
    public List<Object> sections = new ArrayList<>();
    public List<ActivityType.Type> types = new ArrayList<>();

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
        if (item instanceof Activity)
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
                binding = SectionGapBinding.inflate(li, parent, false);
                break;
            case activity:
                binding = SectionBinding.inflate(li, parent, false);
                break;
            case doc:
                binding = SectionDocBinding.inflate(li, parent, false);
                break;
        }
        return new AlarmGroupsHolder(binding, this);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmGroupsHolder holder, int position) {
        holder.bind(position, sections.get(position));
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    /** Since the alarm list will be overall changed when any agenda is edited,
     *  a global notification in change of ui is required. */

    public void setAgenda(Agenda agenda) {
        setAgenda(agenda, -2);
    }
    @SuppressLint("NotifyDataSetChanged")
    public int setAgenda(Agenda agenda, long activityLId) {
        this.agenda = agenda;
        List<Object> list = new ArrayList<>();
        List<ActivityType.Type> types = new ArrayList<>();

        int i = 0, activityPId = -1;
        List[] objlist = agenda.getObjList(true);
        for (Object obj : objlist[0]) {
            list.add(new GapData(i));
            types.add(ActivityType.Type.gap);
            list.add(obj);
            types.add((ActivityType.Type) objlist[1].get(i));
            if (obj instanceof Activity
                    && ((Activity) obj).alarmList.size() > 0
                    && ((Activity) obj).alarmList.get(0).alarmList.activityId == activityLId) {
                activityPId = i;
            }
            i++;
        }
        list.add(new GapData(i));
        types.add(ActivityType.Type.gap);

        int expandId = activityPId == -1 ? -1 : activityPId * 2 + 1;
        if (activityLId != -2)
            id.setId(expandId);

        this.sections = list;
        this.types = types;
        notifyDataSetChanged();

        return expandId;
    }

    public void remove(Object obj, int i) {
        if (obj instanceof Activity)
            ((Activity) obj).visible = false;
        if (obj instanceof _Doc)
            ((_Doc) obj).visible = false;
        toAddSection.setId(0);
        id.setId(-1);
        agenda.agenda.types.remove(i);
        agenda.update();
        setAgenda(agenda);
    }

    /** the index i is the index i IN THE VISUAL LIST. */
    public void insert(ActivityType.Type type, int i) {
        switch (type) {
            case activity:
                Activity gp = Activity.empty();
                agenda.agenda.types.add(i, new ActivityType(type, agenda.activities.size()));
                agenda.activities.add(gp);
                break;
            case doc:
                _Doc doc = _Doc.empty();
                agenda.agenda.types.add(i, new ActivityType(type, agenda.docs.size()));
                agenda.docs.add(doc);
                break;
        }
        toAddSection.setId(0);
        agenda.update();
        setAgenda(agenda);
        id.setId(i * 2 + 1);
    }

    public void setScrollToFunc(Id.IdObj scrollFunc) {
        this.scrollFunc = scrollFunc;
    }

    class AlarmGroupsHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding vbinding;
        private SectionAdapter adapter;

        public AlarmGroupsHolder(ViewDataBinding binding, SectionAdapter adapter) {
            super(binding.getRoot());
            this.vbinding = binding;
            this.adapter = adapter;
        }

        public boolean bind(int i, Object sect) {
            if (sect instanceof GapData)
                return bindGap(i, (GapData) sect);
            if (sect instanceof Activity)
                return bindActivity(i, (Activity) sect);
            if (sect instanceof _Doc)
                return bindDoc(i, (_Doc) sect);
            return false;
        }

        public boolean bindGap(int i, GapData gap) {
            SectionGapBinding binding = (SectionGapBinding) vbinding;

            toAddSection.observe((ov, v)->{
                binding.bar.setVisibility(v == 1 ? View.VISIBLE : View.GONE);
            }, true);
            binding.expandedTab.setVisibility(id.equals(i) ? View.VISIBLE : View.GONE);
            binding.collapsedTab.setVisibility(id.equals(i) ? View.GONE : View.VISIBLE);
            binding.tab.setOnClickListener(
                    (v)-> id.setId(i)
            );
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

            return true;
        }

        public boolean bindActivity(int i, Activity gp) {
            SectionBinding binding = (SectionBinding) vbinding;
            Switch tSwitch = new Switch(false);

            binding.bar.setOnClickListener(
                    (v)->{
                        toAddSection.setId(0);
                        scrollFunc.run(i, i);
                        id.setId(i);
                        tSwitch.setState(false);
                    }
            );
            binding.btnDelete.setOnClickListener(
                    (v)->adapter.remove(gp, i/2)
            );
            boolean expanded = id.equals(i);
            int visibility = expanded ? View.VISIBLE : View.GONE;
            int antiVisibility = !expanded ? View.VISIBLE : View.GONE;
            binding.btnDelete.setVisibility(visibility);
            binding.sectionItems.setVisibility(visibility);
            binding.sectionDetails.setVisibility(antiVisibility);
            AlarmList gpl_alarmL = null;
            if (gp.alarmList.size() > 0) {
                Object[] gpl = gp.getFirstAlarmList();
                gpl_alarmL = (AlarmList)gpl[0];
                LocalDateTime gpl_dt = (LocalDateTime)gpl[1];
                binding.sectdI1.setImageDrawable(gpl_alarmL.alarmList.type.getDrawable());
                //binding.importanceLabel.setColorFilter(gpl.alarmList.importance.getColorPr());
                binding.sectdT1.setText(DateTimesFormatter.getDateTime(gpl_dt));
            } else {
                binding.sectdI1.setVisibility(View.GONE);
                binding.sectdT1.setVisibility(View.GONE);
            }
            SpannableStringBuilder ssb = null;
            if (gpl_alarmL != null)
                ssb = gpl_alarmL.alarmList.getArg(TagType.LOC.name());
            if (gpl_alarmL != null && ssb != null) {
                binding.sectdI2.setImageDrawable(TagType.LOC.getDrawable());
                binding.sectdT2.setText(ssb);
            } else {
                binding.sectdI2.setVisibility(View.GONE);
                binding.sectdT2.setVisibility(View.GONE);
            }

            Drawable icon = gp.activity.getType().getDrawable();
            icon.setColorFilter(
                    gp.activity.getImportance().getColorPr(),
                    PorterDuff.Mode.SRC_IN
            );
            binding.docIcon.setImageDrawable(icon);

            Drawable bg = binding.sectionCard.getBackground();
            bg.setColorFilter(
                    gp.activity.getImportance().getColorPr(),
                    PorterDuff.Mode.SRC_IN
            );//*/
            binding.sectionCard.setBackground(bg);

            EditText docText = binding.docTag;
            associate(docText, gp.activity);
            docText.setFocusableInTouchMode(true);
            docText.setFocusable(id.equals(i));
            docText.setOnClickListener(
                    (v)->{
                        docText.setFocusable(true);
                        toAddSection.setId(0);
                        scrollFunc.run(i, i);
                        id.setId(i);
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
                    gp.activity.descr = new SpannableStringBuilder(docText.getText());
                }
            };
            docText.setTag(textWatcher);
            docText.addTextChangedListener(textWatcher);

            RecyclerView recyclerView = binding.sectionItems;
            recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
            recyclerView.setHasFixedSize(true);

            final SectionItemAdapter adapter = new SectionItemAdapter(
                    (alarmList)-> notifyItemChanged(i), recyclerView, id, i, tSwitch
            );
            tSwitch.observe((state)-> {
                if (!(id.equals(-1) && toAddSection.equals(0)) && id.equals(i))
                    updateLabel(label, label2, false, state,
                            (v)->adapter.removeChecked(), (v)->adapter.editTagOfChecked(),
                            adapter::checkListIsEmpty, adapter.bSwitch);
            }, true);
            recyclerView.setAdapter(adapter);
            adapter.setGroup(gp);

            return true;
        }
        public void associate(EditText editText, _Activity activity) {
            //prevents triggering at the initial change in text (initialization)
            Object tag = editText.getTag();
            if (tag instanceof TextWatcher)
                editText.removeTextChangedListener((TextWatcher) tag);

            editText.setText(activity.descr);
        }

        public boolean bindDoc(int i, _Doc doc) {
            SectionDocBinding binding = (SectionDocBinding) vbinding;

            binding.bar.setOnClickListener(
                    (v)->{
                        if (!id.equals(i)) {
                            toAddSection.setId(0);
                            id.setId(i);
                        }
                    }
            );
            binding.btnDelete.setOnClickListener(
                    (v)->adapter.remove(doc, i/2)
            );
            binding.btnDelete.setVisibility(id.equals(i) ? View.VISIBLE : View.GONE);
            binding.docContentEdittext.setOnClickListener(
                    (v)->{
                        if (!id.equals(i)) {
                            toAddSection.setId(0);
                            id.setId(i);
                        }
                    }
            );
            binding.docContentEdittext.setFocusable(id.equals(i));

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

    /** This must be executed before setAgenda. */
    public void setBinaryLabel(Label lbl, Label lbl2) {
        this.label = lbl;
        this.label2 = lbl2;
        id.observe((oi, i)->{
            boolean collapsed = i == -1 && toAddSection.equals(0);
            updateLabel(this.label, this.label2, collapsed, false,
                    null, null, null, null);
        }, true);
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
        id.setId(-1);
    }
    public void addNewSection(View v) {
        toAddSection.setId(1);
    }


    public static class GapData {
        public int i;

        public GapData(int i) {
            this.i = i;
        }
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

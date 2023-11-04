package com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.data.model.entities._Activity;
import com.aurora.oasisplanner.data.model.pojo.Agenda;
import com.aurora.oasisplanner.data.model.pojo.Activity;
import com.aurora.oasisplanner.data.model.entities._Doc;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.data.util.Id;
import com.aurora.oasisplanner.databinding.SectionBinding;
import com.aurora.oasisplanner.databinding.SectionDocBinding;
import com.aurora.oasisplanner.databinding.SectionGapBinding;
import com.aurora.oasisplanner.util.styling.Resources;
import com.aurora.oasisplanner.util.styling.Styles;

import java.util.ArrayList;
import java.util.List;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.AlarmGroupsHolder> {

    private static final int ID_KEY_SECTIONS = 2;
    private Id id;

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
    @SuppressLint("NotifyDataSetChanged")
    public void setAgenda(Agenda agenda) {
        this.agenda = agenda;
        List<Object> list = new ArrayList<>();
        List<ActivityType.Type> types = new ArrayList<>();

        int i = 0;
        List[] objlist = agenda.getObjList(true);
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
        notifyDataSetChanged();
    }

    public void remove(Object obj, int i) {
        if (obj instanceof Activity)
            ((Activity) obj).visible = false;
        if (obj instanceof _Doc)
            ((_Doc) obj).visible = false;
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
        id.setId(i * 2 + 1);
        agenda.update();
        setAgenda(agenda);
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

            binding.expandedTab.setVisibility(id.equals(i) ? View.VISIBLE : View.GONE);
            binding.collapsedTab.setVisibility(id.equals(i) ? View.GONE : View.VISIBLE);
            binding.tab.setOnClickListener(
                    (v)->id.setId(i)
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

            binding.bar.setOnClickListener(
                    (v)->id.setId(i)
            );
            binding.btnDelete.setOnClickListener(
                    (v)->adapter.remove(gp, i/2)
            );
            boolean expanded = id.equals(i);
            int visibility = expanded ? View.VISIBLE : View.GONE;
            binding.btnDelete.setVisibility(visibility);
            binding.sectionItems.setVisibility(visibility);

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
                    (alarmList)-> notifyItemChanged(i), recyclerView, id, i
            );
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
                        if (!id.equals(i))
                            id.setId(i);
                    }
            );
            binding.btnDelete.setOnClickListener(
                    (v)->adapter.remove(doc, i/2)
            );
            binding.btnDelete.setVisibility(id.equals(i) ? View.VISIBLE : View.GONE);
            binding.docContentEdittext.setOnClickListener(
                    (v)->{
                        if (!id.equals(i))
                            id.setId(i);
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

    public static class GapData {
        public int i;

        public GapData(int i) {
            this.i = i;
        }
    }
}

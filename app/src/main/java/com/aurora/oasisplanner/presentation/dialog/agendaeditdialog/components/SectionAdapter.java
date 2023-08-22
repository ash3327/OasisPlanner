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

import com.aurora.oasisplanner.data.model.pojo.Agenda;
import com.aurora.oasisplanner.data.model.pojo.Group;
import com.aurora.oasisplanner.data.model.entities._Doc;
import com.aurora.oasisplanner.data.tags.GroupType;
import com.aurora.oasisplanner.data.util.Id;
import com.aurora.oasisplanner.databinding.SectionBinding;
import com.aurora.oasisplanner.databinding.SectionDocBinding;
import com.aurora.oasisplanner.databinding.SectionGapBinding;
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
    public List<GroupType.Type> types = new ArrayList<>();

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
        if (item instanceof Group)
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
        switch (GroupType.Type.values()[viewType]) {
            case gap:
                binding = SectionGapBinding.inflate(li, parent, false);
                break;
            case group:
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
        List<GroupType.Type> types = new ArrayList<>();

        int i = 0;
        List[] objlist = agenda.getObjList(true);
        for (Object obj : objlist[0]) {
            list.add(new GapData(i));
            types.add(GroupType.Type.gap);
            list.add(obj);
            types.add((GroupType.Type) objlist[1].get(i));
            i++;
        }
        list.add(new GapData(i));
        types.add(GroupType.Type.gap);

        this.sections = list;
        this.types = types;
        notifyDataSetChanged();
    }

    public void remove(Object obj, int i) {
        if (obj instanceof Group)
            ((Group) obj).visible = false;
        if (obj instanceof _Doc)
            ((_Doc) obj).visible = false;
        agenda.agenda.types.remove(i);
        agenda.update();
        setAgenda(agenda);
    }

    /** the index i is the index i IN THE VISUAL LIST. */
    public void insert(GroupType.Type type, int i) {
        switch (type) {
            case group:
                Group gp = Group.empty();
                agenda.agenda.types.add(i, new GroupType(type, agenda.groups.size()));
                agenda.groups.add(gp);
                break;
            case doc:
                _Doc doc = _Doc.empty();
                agenda.agenda.types.add(i, new GroupType(type, agenda.docs.size()));
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
            if (sect instanceof Group)
                return bindGroup(i, (Group) sect);
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
                             adapter.insert(GroupType.Type.group, gap.i);
                    }
            );
            binding.btnAddDoc.setOnClickListener(
                    (v)->{
                        if (id.equals(i))
                            adapter.insert(GroupType.Type.doc, gap.i);
                    }
            );

            return true;
        }

        public boolean bindGroup(int i, Group gp) {
            SectionBinding binding = (SectionBinding) vbinding;

            binding.bar.setOnClickListener(
                    (v)->id.setId(i)
            );
            binding.btnDelete.setOnClickListener(
                    (v)->adapter.remove(gp, i/2)
            );
            binding.btnDelete.setVisibility(id.equals(i) ? View.VISIBLE : View.GONE);

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

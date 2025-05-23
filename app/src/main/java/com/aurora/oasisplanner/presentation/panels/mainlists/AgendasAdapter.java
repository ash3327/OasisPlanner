package com.aurora.oasisplanner.presentation.panels.mainlists;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.OasisApp;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.databinding.BoxAgendaBinding;
import com.aurora.oasisplanner.databinding.HeaderImageBinding;
import com.aurora.oasisplanner.presentation.dialogs.agendaeditdialog.util.AgendaAccessUtil;
import com.aurora.oasisplanner.presentation.panels.utils.PaddingItemDecoration;
import com.aurora.oasisplanner.presentation.util.Id;
import com.aurora.oasisplanner.util.styling.Resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AgendasAdapter extends RecyclerView.Adapter<AgendasAdapter.AgendasHolder> {

    private static final int ID_KEY_AGENDAGROUPS = 2;
    private Id id;

    {
        setHasStableIds(true);
        id = new Id(-1, ID_KEY_AGENDAGROUPS);//2
        id.observe((oldId, newId)->{
            if(oldId >= 0) notifyItemChanged(oldId);
            if(newId >= 0) notifyItemChanged(newId);
        });
    }

    public List<Object> agendas = new ArrayList<>();
    public int agendaCount = 0;
    public boolean groupIsOn = true;

    public AgendasAdapter(RecyclerView recyclerView, boolean hasBoundary) {
        if (!Objects.equals(recyclerView.getTag("hasDivider".hashCode()), true)) {
            recyclerView.addItemDecoration(
                    new PaddingItemDecoration(
                            R.dimen.paddingBoxesDecorationDefault,
                            hasBoundary ? R.dimen.paddingItemDecorationEdge : R.dimen.paddingBoxesDecorationDefault
                    )
            );
            recyclerView.setTag("hasDivider".hashCode(), true);
        }
    }

    enum ViewType {GroupData, Agenda}
    @Override
    public int getItemViewType(int position) {
        Object item = agendas.get(position);
        if (item instanceof Agenda)
            return ViewType.Agenda.ordinal();
        if (item instanceof GroupData)
            return ViewType.GroupData.ordinal();
        return -1;
    }

    @Override
    public long getItemId(int position) {
        Object item = agendas.get(position);
        int numClasses = 2;
        if (item instanceof Agenda)
            return  ((Agenda) item).agenda.id * numClasses;
        if (item instanceof GroupData)
            return (long) ((GroupData) item).groupName.hashCode() * numClasses + 1;
        return -1;
    }

    @NonNull
    @Override
    public AgendasHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding = null;
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        switch (ViewType.values()[viewType]) {
            case Agenda:
                binding = BoxAgendaBinding.inflate(li, parent, false);
                break;
            case GroupData:
                binding = HeaderImageBinding.inflate(li, parent, false);
                break;
        }
        return new AgendasHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AgendasHolder holder, int position) {
        holder.bind(position, agendas.get(position));

        if (position == getItemCount()-1)
            holder.itemView.setPadding(
                    holder.itemView.getPaddingLeft(),
                    holder.itemView.getPaddingTop(),
                    holder.itemView.getPaddingRight(),
                    holder.itemView.getPaddingTop()
                            +(int)(50*holder.itemView.getContext().getResources().getDisplayMetrics().density));
        else
            holder.itemView.setPadding(
                    holder.itemView.getPaddingLeft(),
                    holder.itemView.getPaddingTop(),
                    holder.itemView.getPaddingRight(),
                    holder.itemView.getPaddingTop());

    }

    @Override
    public int getItemCount() {
        return agendas.size();
    }
    public int getAgendasCount() {
        return agendaCount;
    }

    /** Since the agenda list will be overall changed when any agenda is edited,
     *  a global notification in change of ui is required. */
    @SuppressLint("NotifyDataSetChanged")
    public void setAgendas(List<Agenda> agendas) {
        this.agendas = new ArrayList<>();

        for (Agenda item : agendas) {
            if (!AgendaAccessUtil.agendaIsPlaceholder(item))
                this.agendas.add(item);
        }
        agendaCount = agendas.size();
        if (onChangeListener != null)
            onChangeListener.run(agendaCount);

        notifyDataSetChanged();
    }

    private OnChangeListener onChangeListener;
    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public void scrollToItem(int item, LinearLayoutManager llm) {
        if (item == -1)
            item = agendas.size()-1;
        llm.scrollToPositionWithOffset(item, 0);
    }

    static class AgendasHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding vbinding;
        private Vibrator vibrator;

        public AgendasHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.vbinding = binding;
            this.vibrator = (Vibrator) OasisApp.getContext().getSystemService(Context.VIBRATOR_SERVICE);
        }

        public boolean bind(int i, Object agendaGp) {
            switch (ViewType.values()[getItemViewType()]) {
                case Agenda:
                    return bindAgenda(i, (Agenda) agendaGp);
                case GroupData:
                    return bindGroup(i, (GroupData) agendaGp);
            }
            return false;
        }

        public boolean bindAgenda(int i, Agenda agenda) {
            BoxAgendaBinding binding = (BoxAgendaBinding) vbinding;

            binding.bar.setOnClickListener(
                    (v)-> AppModule.retrieveAgendaUseCases().edit(agenda.agenda.id, -1, -1)
            );
            binding.barIcon.setImageResource(R.drawable.ic_agendatype_calendar);

            binding.triangle.setVisibility(View.VISIBLE);
            binding.bar.setBackground(Resources.getDrawable(R.drawable.border_grey));
//            binding.triangle.getBackground().setColorFilter(agenda.getImportance().getColorPr(), PorterDuff.Mode.SRC_OVER);
            binding.setFgColor(Color.BLACK);

            binding.barTitle.setText(agenda.getTitle());
//            SpannableStringBuilder content = agenda.getContents(false);
//            boolean hasDesc = content != null && !content.toString().isEmpty();
//            if (hasDesc)
//                binding.barDescriptionText.setText(content);
//            binding.barDescTextLay.setVisibility(hasDesc ? View.VISIBLE : View.GONE);
            binding.barDescTextLay.setVisibility(View.GONE);
            binding.barLocSubBar.setVisibility(View.GONE);
//
//            binding.boxAgendaTags.setInputType(InputType.TYPE_NULL);
//            String s = agenda.getTagsString();
//            if (s == null)
//                binding.boxAgendaTagsScroll.setVisibility(View.GONE);
//            else {
//                binding.boxAgendaTagsScroll.setVisibility(View.VISIBLE);
//                binding.boxAgendaTags.setTags(s);
//            }

            return true;
        }

        public boolean bindGroup(int i, GroupData groupData) {
            HeaderImageBinding binding = (HeaderImageBinding) vbinding;

//            binding.setShowVLine(i != 0);
//            binding.textView.setText(DateTimesFormatter.getYM(groupData.month));
//            Image.monthBannerOf(groupData.month.getMonth()).showImageOn(binding.imgBg);

            return true;
        }
    }

    public static class GroupData {
        private String groupName;

        public GroupData(String groupName) {
            this.groupName = groupName;
        }
    }

    public interface OnChangeListener {
        void run(int size);
    }
}

package com.aurora.oasisplanner.presentation.ui.alarms.components;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.pojo.events.Alarm;
import com.aurora.oasisplanner.data.structure.Image;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.util.Id;
import com.aurora.oasisplanner.databinding.BoxEndBinding;
import com.aurora.oasisplanner.databinding.BoxEventBinding;
import com.aurora.oasisplanner.databinding.DayLabelBinding;
import com.aurora.oasisplanner.databinding.HeaderImageBinding;
import com.aurora.oasisplanner.presentation.ui.dividers.PaddingItemDecoration;
import com.aurora.oasisplanner.util.styling.DateTimesFormatter;
import com.aurora.oasisplanner.util.styling.Resources;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AlarmsAdapter extends RecyclerView.Adapter<AlarmsAdapter.AlarmsHolder> {

    private static final int ID_KEY_ALARMGROUPS = 0;
    private Id id;

    {
        setHasStableIds(true);
        id = new Id(-1, ID_KEY_ALARMGROUPS);
        id.observe((oldId, newId)->{
            if(oldId >= 0) notifyItemChanged(oldId);
            if(newId >= 0) notifyItemChanged(newId);
        });
    }

    public List<Object> alarms = new ArrayList<>();

    public AlarmsAdapter(RecyclerView recyclerView) {
        if (!Objects.equals(recyclerView.getTag("hasDivider".hashCode()), true)) {
            recyclerView.addItemDecoration(
                    new PaddingItemDecoration(
                            R.dimen.paddingBoxesDecorationDefault,
                            R.dimen.paddingItemDecorationEdge
                    )
            );
            recyclerView.setTag("hasDivider".hashCode(), true);
        }
    }

    enum ViewType {MonthData, DayData, Alarm, EndData}
    @Override
    public int getItemViewType(int position) {
        Object item = alarms.get(position);
        if (item instanceof Alarm)
            switch (((Alarm) item).getType()) {
                case notif:
                case agenda:
                case todo: return ViewType.Alarm.ordinal();
                default:
                    throw new RuntimeException(
                            "Improper AlarmType "+((Alarm) item).getType().name()+" at item "+position
                    );
            }
        if (item instanceof MonthData)
            return ViewType.MonthData.ordinal();
        if (item instanceof DayData)
            return ViewType.DayData.ordinal();
        if (item instanceof EndData)
            return ViewType.EndData.ordinal();
        return -1;
    }

    @Override
    public long getItemId(int position) {
        Object item = alarms.get(position);
        int numClasses = 3;
        if (item instanceof Alarm)
            return  (
                            ((Alarm) item).getDateTime().toEpochSecond(ZoneOffset.UTC) * AlarmType.values().length
                            + ((Alarm) item).getType().ordinal()
                    ) * numClasses;
        if (item instanceof MonthData)
            return ((MonthData) item).month.toEpochDay() * numClasses + 1;
        if (item instanceof DayData)
            return ((DayData) item).day.toEpochDay() * numClasses + 2;
        return -1;
    }

    @NonNull
    @Override
    public AlarmsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding = null;
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        switch (ViewType.values()[viewType]) {
            case Alarm:
                binding = BoxEventBinding.inflate(li, parent, false);
                break;
            case MonthData:
                binding = HeaderImageBinding.inflate(li, parent, false);
                break;
            case DayData:
                binding = DayLabelBinding.inflate(li, parent, false);
                break;
            case EndData:
                binding = BoxEndBinding.inflate(li, parent, false);
        }
        return new AlarmsHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmsHolder holder, int position) {
        holder.bind(position, alarms.get(position));

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
        return alarms.size();
    }

    /** Since the alarm list will be overall changed when any agenda is edited,
     *  a global notification in change of ui is required. */
    @SuppressLint("NotifyDataSetChanged")
    public void setAlarms(List<Alarm> alarms) {
        List<Object> list = new ArrayList<>();

        LocalDate currentMonth = null;
        LocalDate currentDay = null;
        for (Alarm gp : alarms) {
            LocalDate date = gp.getDate();
            LocalDate month = date.withDayOfMonth(1);
            if (!month.equals(currentMonth)) {
                list.add(new MonthData(month));
                currentMonth = month;
            }
            if (!date.equals(currentDay)) {
                list.add(new DayData(date));
                currentDay = date;
            }
            list.add(gp);
        }
        list.add(new EndData());
        this.alarms = list;
        if (onChangeListener != null)
            onChangeListener.run(list.size());

        notifyDataSetChanged();
    }

    private OnChangeListener onChangeListener;
    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    public LocalDate getLocalDateOfItem(Object item) {
        LocalDate ldt = LocalDate.now();
        if (item instanceof Alarm)
            ldt = ((Alarm) item).getDateTime().toLocalDate();
        else if (item instanceof MonthData)
            ldt = ((MonthData) item).month;
        else if (item instanceof DayData)
            ldt = ((DayData) item).day;
        return ldt;
    }

    public int getMonthOfItem(Object item) {
        LocalDate ldt = getLocalDateOfItem(item);
        return ldt.getMonthValue() + ldt.getYear() * 12;
    }

    /** output format: year * 12 + month */
    public int getCurrentMonth(RecyclerView recyclerView) {
        LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
        assert lm != null;
        int firstVisiblePos = lm.findFirstVisibleItemPosition();
        Object item = alarms.get(firstVisiblePos);
        return getMonthOfItem(item);
    }

    /** input format: year * 12 + month */
    public int firstItemWithMonth(int mo) {
        int newPos = -1;
        for (int i = 0 ; i < alarms.size() ; i++) {
            if (getMonthOfItem(alarms.get(i)) >= mo) {
                newPos = i;
                break;
            }
        }
        return newPos;
    }

    public void scrollToItem(int item, LinearLayoutManager llm) {
        if (item == -1)
            item = alarms.size()-1;
        llm.scrollToPositionWithOffset(item, 0);
    }

    public void scrollToPrevMonth(RecyclerView recyclerView) {
        int mo = getCurrentMonth(recyclerView);
        LinearLayoutManager llm = (LinearLayoutManager)recyclerView.getLayoutManager();
        assert llm != null;
        scrollToItem(firstItemWithMonth(mo-1), llm);
    }
    public void scrollToNextMonth(RecyclerView recyclerView) {
        int mo = getCurrentMonth(recyclerView);
        LinearLayoutManager llm = (LinearLayoutManager)recyclerView.getLayoutManager();
        assert llm != null;
        scrollToItem(firstItemWithMonth(mo+1), llm);
    }

    static class AlarmsHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding vbinding;

        public AlarmsHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.vbinding = binding;
        }

        public boolean bind(int i, Object alarmGp) {
            switch (ViewType.values()[getItemViewType()]) {
                case Alarm:
                    return bindAlarm(i, (Alarm) alarmGp);
                case MonthData:
                    return bindMonth(i, (MonthData) alarmGp);
                case DayData:
                    return bindDay(i, (DayData) alarmGp);
                case EndData:
                    return true;
            }
            return false;
        }

        public boolean bindAlarm(int i, Alarm alarm) {
            BoxEventBinding binding = (BoxEventBinding) vbinding;

            binding.bar.setOnClickListener(
                    (v)-> AppModule.retrieveAgendaUseCases().edit(alarm.getAgendaId(), alarm.getActivityId(), alarm.getEventId())
            );
            binding.boxToptag.setText(DateTimesFormatter.toTime12h(alarm.getDateTime().toLocalTime()));
            binding.boxBottomtag.setText(alarm.getType().toString());
            binding.barIcon.setImageDrawable(alarm.getType().getSimpleDrawable());
            if (alarm.getType() == AlarmType.agenda) {
                binding.triangle.setVisibility(View.GONE);
                binding.bar.getBackground().setColorFilter(alarm.getImportance().getColorPr(), PorterDuff.Mode.SRC_OVER);
                binding.setFgColor(Color.WHITE);
            } else {
                binding.triangle.setVisibility(View.VISIBLE);
                binding.bar.setBackground(Resources.getDrawable(R.drawable.border_grey));
                binding.triangle.getBackground().setColorFilter(alarm.getImportance().getColorPr(), PorterDuff.Mode.SRC_OVER);
                binding.setFgColor(Color.BLACK);
            }
            binding.circ.setColorFilter(alarm.getImportance().getColorPr(), PorterDuff.Mode.SRC_OVER);
            binding.setIsTask(alarm.getType() == AlarmType.todo);

            binding.barTitle.setText(alarm.getTitle());
            binding.barDescriptionText.setText(alarm.getContents(false));
            SpannableStringBuilder loc = alarm.getLoc();
            if (loc == null)
                binding.barLocSubBar.setVisibility(View.GONE);
            else {
                binding.barLocSubBar.setVisibility(View.VISIBLE);
                binding.barLocText.setText(loc);
            }
            binding.boxEventTags.setInputType(InputType.TYPE_NULL);
            String s = alarm.getTagsString();
            if (s == null)
                binding.boxEventTagsScroll.setVisibility(View.GONE);
            else {
                binding.boxEventTagsScroll.setVisibility(View.VISIBLE);
                binding.boxEventTags.setTags(s);
            }

            return true;
        }

        public boolean bindMonth(int i, MonthData monthData) {
            HeaderImageBinding binding = (HeaderImageBinding) vbinding;

            binding.setShowVLine(i != 0);
            binding.textView.setText(DateTimesFormatter.getYM(monthData.month));
            Image.monthBannerOf(monthData.month.getMonth()).showImageOn(binding.imgBg);

            return true;
        }
        public boolean bindDay(int i, DayData dayData) {
            DayLabelBinding binding = (DayLabelBinding) vbinding;

            binding.tvDayOfWeek.setText(DateTimesFormatter.getWEEK(dayData.day));
            String dayStr = DateTimesFormatter.toDate(dayData.day);
            if (LocalDate.now().isEqual(dayData.day))
                dayStr += "  "+Resources.getString(R.string.bar_today);
            binding.tvDayLabel.setText(dayStr);

            return true;
        }
    }

    public static class MonthData {
        private LocalDate month;

        public MonthData(LocalDate month) {
            this.month = month;
        }
    }

    public static class DayData {
        private LocalDate day;

        public DayData(LocalDate day) {
            this.day = day;
        }
    }

    public static class EndData {}

    public interface OnChangeListener {
        void run(int size);
    }
}

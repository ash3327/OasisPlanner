package com.aurora.oasisplanner.presentation.ui.alarms.components;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities._Alarm;
import com.aurora.oasisplanner.data.structure.Image;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.util.Id;
import com.aurora.oasisplanner.databinding.BoxEventBinding;
import com.aurora.oasisplanner.databinding.BoxNotifBinding;
import com.aurora.oasisplanner.databinding.BoxTaskBinding;
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

    enum ViewType {MonthData, DayData, AlarmNotif, AlarmEvent, AlarmTask}
    @Override
    public int getItemViewType(int position) {
        Object item = alarms.get(position);
        if (item instanceof _Alarm)
            switch (((_Alarm) item).type) {
                case notif: return ViewType.AlarmNotif.ordinal();
                case agenda: return ViewType.AlarmEvent.ordinal();
                case todo: return ViewType.AlarmTask.ordinal();
                default:
                    throw new RuntimeException(
                            "Improper AlarmType "+((_Alarm) item).type.name()+" at item "+position
                    );
            }
        if (item instanceof MonthData)
            return ViewType.MonthData.ordinal();
        if (item instanceof DayData)
            return ViewType.DayData.ordinal();
        return -1;
    }

    @Override
    public long getItemId(int position) {
        Object item = alarms.get(position);
        int numClasses = 3;
        if (item instanceof _Alarm)
            return  (
                            ((_Alarm) item).datetime.toEpochSecond(ZoneOffset.UTC) * AlarmType.values().length
                            + ((_Alarm) item).type.ordinal()
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
            case AlarmNotif:
                binding = BoxNotifBinding.inflate(li, parent, false);
                break;
            case AlarmEvent:
                binding = BoxEventBinding.inflate(li, parent, false);
                break;
            case AlarmTask:
                binding = BoxTaskBinding.inflate(li, parent, false);
                break;
            case MonthData:
                binding = HeaderImageBinding.inflate(li, parent, false);
                break;
            case DayData:
                binding = DayLabelBinding.inflate(li, parent, false);
                break;
        }
        return new AlarmsHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmsHolder holder, int position) {
        holder.bind(position, alarms.get(position));
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }

    /** Since the alarm list will be overall changed when any agenda is edited,
     *  a global notification in change of ui is required. */
    @SuppressLint("NotifyDataSetChanged")
    public void setAlarms(List<_Alarm> alarms) {
        List<Object> list = new ArrayList<>();

        LocalDate currentMonth = null;
        LocalDate currentDay = null;
        for (_Alarm gp : alarms) {
            LocalDate date = gp.date;
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
        this.alarms = list;
        if (onChangeListener != null)
            onChangeListener.run(list.size());

        notifyDataSetChanged();
    }

    private OnChangeListener onChangeListener;
    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    static class AlarmsHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding vbinding;

        public AlarmsHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.vbinding = binding;
        }

        public boolean bind(int i, Object alarmGp) {
            switch (ViewType.values()[getItemViewType()]) {
                case AlarmNotif:
                    return bindAlarmNotif(i, (_Alarm) alarmGp);
                case AlarmEvent:
                    return bindAlarmEvent(i, (_Alarm) alarmGp);
                case AlarmTask:
                    return bindAlarmTask(i, (_Alarm) alarmGp);
                case MonthData:
                    return bindMonth(i, (MonthData) alarmGp);
                case DayData:
                    return bindDay(i, (DayData) alarmGp);
            }
            return false;
        }

        public boolean bindAlarmNotif(int i, _Alarm alarm) {
            BoxNotifBinding binding = (BoxNotifBinding) vbinding;

            binding.bar.setOnClickListener(
                    (v)-> AppModule.retrieveAgendaUseCases().editAgendaUseCase.invoke(alarm.agendaId)
            );
            binding.boxToptag.setText(DateTimesFormatter.toTime12h(alarm.datetime.toLocalTime()));
            binding.boxBottomtag.setText(Resources.getString(R.string.bar_notif));
            binding.barIcon.setImageDrawable(alarm.type.getSimpleDrawable());
            binding.triangle.getBackground().setColorFilter(alarm.importance.getColorPr(), PorterDuff.Mode.SRC_OVER);
            binding.barTitle.setText(alarm.title);
            binding.barDescriptionText.setText(alarm.getContents(false));

            return true;
        }

        public boolean bindAlarmEvent(int i, _Alarm alarm) {
            BoxEventBinding binding = (BoxEventBinding) vbinding;

            binding.bar.setOnClickListener(
                    (v)-> AppModule.retrieveAgendaUseCases().editAgendaUseCase.invoke(alarm.agendaId)
            );
            binding.boxToptag.setText(DateTimesFormatter.toTime12h(alarm.datetime.toLocalTime()));
            binding.boxBottomtag.setText(Resources.getString(R.string.bar_notif));
            binding.barIcon.setImageDrawable(alarm.type.getSimpleDrawable());
            binding.bar.getBackground().setColorFilter(alarm.importance.getColorPr(), PorterDuff.Mode.SRC_OVER);
            binding.barTitle.setText(alarm.title);
            binding.barDescriptionText.setText(alarm.getContents(false));

            return true;
        }

        public boolean bindAlarmTask(int i, _Alarm alarm) {
            BoxTaskBinding binding = (BoxTaskBinding) vbinding;

            binding.bar.setOnClickListener(
                    (v)-> AppModule.retrieveAgendaUseCases().editAgendaUseCase.invoke(alarm.agendaId)
            );
            binding.boxToptag.setText(Resources.getString(R.string.bar_task));
            binding.boxBottomtag.setText(DateTimesFormatter.toTime12h(alarm.datetime.toLocalTime()));
            binding.barIcon.setImageDrawable(alarm.type.getSimpleDrawable());
            binding.triangle.getBackground().setColorFilter(alarm.importance.getColorPr(), PorterDuff.Mode.SRC_OVER);
            binding.barTitle.setText(alarm.title);
            binding.barDescriptionText.setText(alarm.getContents(false));

            return true;
        }

        public boolean bindMonth(int i, MonthData monthData) {
            HeaderImageBinding binding = (HeaderImageBinding) vbinding;

            binding.textView.setText(DateTimesFormatter.getYM(monthData.month));
            Image.monthBannerOf(monthData.month.getMonth()).showImageOn(binding.imgBg);

            return true;
        }
        public boolean bindDay(int i, DayData dayData) {
            DayLabelBinding binding = (DayLabelBinding) vbinding;

            binding.tvDayOfWeek.setText(DateTimesFormatter.getWEEK(dayData.day));
            binding.tvDayLabel.setText(DateTimesFormatter.toDate(dayData.day));

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

    public interface OnChangeListener {
        void run(int size);
    }
}

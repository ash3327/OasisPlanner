package com.aurora.oasisplanner.presentation.panels.homepage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
import com.aurora.oasisplanner.activities.OasisApp;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.pojo.events.Alarm;
import com.aurora.oasisplanner.data.structure.Image;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.databinding.VertBoxEndBinding;
import com.aurora.oasisplanner.databinding.VertBoxEventBinding;
import com.aurora.oasisplanner.databinding.VertDayLabelBinding;
import com.aurora.oasisplanner.databinding.HeaderImageBinding;
import com.aurora.oasisplanner.presentation.panels.utils.PaddingItemDecoration;
import com.aurora.oasisplanner.presentation.util.Id;
import com.aurora.oasisplanner.util.Configs;
import com.aurora.oasisplanner.util.styling.DateTimesFormatter;
import com.aurora.oasisplanner.util.styling.Resources;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class HomeAlarmsAdapter extends RecyclerView.Adapter<HomeAlarmsAdapter.AlarmsHolder> {

    private static final int ID_KEY_ALARMGROUPS = 0;
    private Id id;

    {
        setHasStableIds(true);
        id = new Id(-1, ID_KEY_ALARMGROUPS);//0
        id.observe((oldId, newId)->{
            if(oldId >= 0) notifyItemChanged(oldId);
            if(newId >= 0) notifyItemChanged(newId);
        });
    }

    public List<Object> alarms = new ArrayList<>();
    private int size = 0;
    public boolean monthIsOn = true;
    private boolean forTasks;

    public HomeAlarmsAdapter(RecyclerView recyclerView, boolean hasBoundary, boolean forTasks) {
        this.forTasks = forTasks;
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
                binding = VertBoxEventBinding.inflate(li, parent, false);
                break;
            case MonthData:
                binding = HeaderImageBinding.inflate(li, parent, false);
                break;
            case DayData:
                binding = VertDayLabelBinding.inflate(li, parent, false);
                break;
            case EndData:
                binding = VertBoxEndBinding.inflate(li, parent, false);
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
    public int getAlarmsCount() { return size; }

    /** Since the alarm list will be overall changed when any agenda is edited,
     *  a global notification in change of ui is required. */
    @SuppressLint("NotifyDataSetChanged")
    public void setAlarms(List<Alarm> alarms) {
        if (forTasks)
            this.alarms = alarms.stream().filter((_alarm)->_alarm.getType()==AlarmType.todo&&!_alarm.alarm.isFinished()).collect(Collectors.toList());
        else
            this.alarms = alarms.stream().filter((_alarm)->!_alarm.alarm.isFinished()).collect(Collectors.toList());

        size = this.alarms.size();
        // IMPORTANT: THIS CODE IS ADDED BECAUSE CANNOT FIND REASON FOR THE UI BREAKING FOR THE LAST ELEMENT.
        this.alarms.add(new EndData());
        if (onChangeListener != null)
            onChangeListener.run(this.alarms.size());

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
        private Vibrator vibrator;

        public AlarmsHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.vbinding = binding;
            this.vibrator = (Vibrator) OasisApp.getContext().getSystemService(Context.VIBRATOR_SERVICE);
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
            VertBoxEventBinding binding = (VertBoxEventBinding) vbinding;

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
            if (alarm.getType() == AlarmType.todo) {
                binding.circ.setImageResource(alarm.alarm.isFinished() ? R.drawable.radio_checked : R.drawable.radio_unchecked);
                binding.circ.clearColorFilter();
                binding.circ.setOnClickListener((v)-> {
                    boolean hasFinished = alarm.alarm.isFinished();
                    alarm.putArg(_Alarm.ArgType.STATE, hasFinished ? "UNFINISHED" : "FINISHED");

                    if (!hasFinished) {
                        if (Configs.clickSoundIsOn) {
                            final MediaPlayer mp = MediaPlayer.create(v.getContext(), R.raw.done_bellring);
                            mp.setOnCompletionListener(MediaPlayer::release);
                            mp.start();
                        }
                        if (vibrator != null && vibrator.hasVibrator()) {
                            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
                        }
                    }
                    binding.circ.setImageResource(alarm.alarm.isFinished() ? R.drawable.radio_checked : R.drawable.radio_unchecked);
                    AppModule.retrieveAlarmUseCases().putWith(alarm);
                });
            } else {
                binding.circ.setImageResource(R.drawable.shp_circ_filled);
                binding.circ.setColorFilter(alarm.getImportance().getColorPr(), PorterDuff.Mode.SRC_OVER);
                ViewGroup.LayoutParams lp = binding.circ.getLayoutParams();
                lp.height = lp.width = (int)Resources.getDimension(
                        alarm.getType() == AlarmType.notif ? R.dimen.icon_height : R.dimen.medium_small_icon_height
                );
                binding.circ.setLayoutParams(lp);
            }

            binding.barTitle.setText(alarm.getTitle());
            SpannableStringBuilder content = alarm.getContents(false);
            boolean hasDesc = content != null && !content.toString().isEmpty();
            if (hasDesc)
                binding.barDescriptionText.setText(content);
            binding.barDescTextLay.setVisibility(hasDesc ? View.VISIBLE : View.GONE);
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
            VertDayLabelBinding binding = (VertDayLabelBinding) vbinding;

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

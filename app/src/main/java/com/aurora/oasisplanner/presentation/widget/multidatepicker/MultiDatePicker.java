package com.aurora.oasisplanner.presentation.widget.multidatepicker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.databinding.MultiDatePickerBinding;
import com.aurora.oasisplanner.util.styling.DateTimesFormatter;
import com.aurora.oasisplanner.util.styling.Resources;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Locale;

public class MultiDatePicker extends LinearLayout implements Updatable {
    private MultiDatePickerBinding binding;
    private LocalDate now = LocalDate.now();
    private LocalDate focusedMoment;
    private int dowOfMonthStart, numDaysInPrevMonth, numDaysInThisMonth, month, year;
    private ArrayAdapter<CalendarDayView> adapter;
    private boolean editState = true, inEditState = false;
    private boolean prevMonthInvalid, nextMonthInvalid;
    private VelocityTracker mVelocityTracker = null;
    private float lastXVel = 0, lastYVel = 0;
    private String[] weekDayName;
    {
        if (!isInEditMode())
            weekDayName = Resources.getStringArr(R.array.weekdays);
    }

    private static final int
            REGULAR = 0,
            SUBHIGHLIGHTED = 1,
            HIGHLIGHTED = 2,
            PREVMONTH = -1,
            THISMONTHINVALID = -2,
            NEXTMONTH = -3,
            WEEKLABEL = -4;
    private static final int
            DIFFMONTH_REGULAR = PREVMONTH,
            DIFFMONTH_SUBHIGLIGHTED = NEXTMONTH;

    private int baseColor, invalidColor,
            regularBgColor, regularTextColor,
            diffMonthBgColor, diffMonthTextColor,
            weekDayBgColor, weekDayTextColor,
            highlightedBgColor, highlightedTextColor,
            subHighlightedBgColor, subHighlightedTextColor,
            diffMonthSubHighlightedBgColor, diffMonthSubHighlightedTextColor;
    private float cellPadding, rowPadding;

    public ArrayList<LocalDate> selected = new ArrayList<>();
    public LocalDate minDateAllowed = null, maxDateAllowed = null;
    public boolean multiSelectable = true;

    public MultiDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private static final float THRESHOLD = 0.01f;
    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context, AttributeSet attrs) {
        binding = MultiDatePickerBinding.inflate(LayoutInflater.from(context), this, true);

        setMonth(now.getYear(), now.getMonthValue());
        selected.add(focusedMoment);
        if (isInEditMode())
            selected.add(LocalDate.of(2023, 7, 16));

        // attributes handling
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiDatePicker);
        baseColor = a.getColor(R.styleable.MultiDatePicker_baseColor, Color.BLACK);
        invalidColor = a.getColor(R.styleable.MultiDatePicker_invalidColor, Color.GRAY);

        regularBgColor = a.getColor(R.styleable.MultiDatePicker_regularBgColor, Color.LTGRAY);
        regularTextColor = a.getColor(R.styleable.MultiDatePicker_regularTextColor, Color.BLACK);

        diffMonthBgColor = a.getColor(R.styleable.MultiDatePicker_diffMonthBgColor, Color.WHITE);
        diffMonthTextColor = a.getColor(R.styleable.MultiDatePicker_diffMonthTextColor, Color.GRAY);

        weekDayBgColor = a.getColor(R.styleable.MultiDatePicker_weekDayBgColor, Color.WHITE);
        weekDayTextColor = a.getColor(R.styleable.MultiDatePicker_weekDayTextColor, Color.GRAY);

        highlightedBgColor = a.getColor(R.styleable.MultiDatePicker_highlightedBgColor, Color.YELLOW);
        highlightedTextColor = a.getColor(R.styleable.MultiDatePicker_highlightedTextColor, Color.BLACK);

        subHighlightedBgColor = a.getColor(R.styleable.MultiDatePicker_subHighlightedBgColor, Color.GREEN);
        subHighlightedTextColor = a.getColor(R.styleable.MultiDatePicker_subHighlightedTextColor, Color.BLACK);

        diffMonthSubHighlightedBgColor = a.getColor(R.styleable.MultiDatePicker_diffMonthSubHighlightedBgColor, Color.GREEN);
        diffMonthSubHighlightedTextColor = a.getColor(R.styleable.MultiDatePicker_diffMonthSubHighlightedTextColor, Color.BLACK);

        cellPadding = a.getDimension(R.styleable.MultiDatePicker_padding, 2);
        rowPadding = a.getDimension(R.styleable.MultiDatePicker_rowPadding, 2);

        a.recycle();

        setBaseColor(baseColor);
        binding.monthTitle.setOnClickListener((v)->{
            setMonth(now.getYear(), now.getMonthValue());
            refresh();
        });

        // Create an adapter for the GridView
        adapter = new ArrayAdapter<CalendarDayView>(getContext(), 0) {
            @SuppressLint("ClickableViewAccessibility")
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                CalendarDayView view;
                if (convertView == null) {
                    view = new CalendarDayView(getContext());
                    view.setLayoutParams(new GridView.LayoutParams(
                            GridView.LayoutParams.MATCH_PARENT,
                            GridView.LayoutParams.WRAP_CONTENT));
                } else {
                    view = (CalendarDayView) convertView;
                }
                int[] day = getDay(position);

                if (day[0] == WEEKLABEL)
                    view.setText(
                            isInEditMode() ?
                            (DayOfWeek.of(day[1])+"").substring(0,3) :
                            weekDayName[day[1] % 7]
                    );
                else view.setDay(day[1]);

                int bgColor = diffMonthBgColor, textColor = diffMonthTextColor;
                switch (day[0]) {
                    case REGULAR:
                        textColor = regularTextColor; break;
                    case HIGHLIGHTED:
                        textColor = highlightedTextColor; break;
                    case SUBHIGHLIGHTED:
                        textColor = subHighlightedTextColor; break;
                    case WEEKLABEL:
                        bgColor = weekDayBgColor; textColor = weekDayTextColor;
                        break;
                }

                boolean hasFront = true, hasEnd = true;
                if (day[0] != WEEKLABEL) {
                    LocalDate date = LocalDate.of(day[3], day[2], day[1]);
                    if (day[0] != REGULAR) {
                        hasFront = selected.contains(date.minusDays(1));
                        hasEnd = selected.contains(date.plusDays(1));
                    }
                    if (day[0] >= REGULAR) { // days in this month
                        bgColor = regularBgColor;
                        view.setOnClickListener(
                                (e) -> setDate(date, true)
                        );
                        view.setOnLongClickListener(
                                (e) -> {
                                    inEditState = true;
                                    setDate(date, false);
                                    return true;
                                }
                        );
                        view.setEnabled(true);
                    } else
                        view.setEnabled(false);
                }

                view.setTheBackgroundColor(bgColor);
                view.setBackground(
                        day[0] >= SUBHIGHLIGHTED ?
                                subHighlightedBgColor :
                                day[0] == DIFFMONTH_SUBHIGLIGHTED ?
                                    diffMonthSubHighlightedBgColor :
                                    0,
                        hasFront,
                        hasEnd
                );
                if (day[0] == HIGHLIGHTED)
                    view.setForeground(highlightedBgColor);
                else view.setForeground(Color.TRANSPARENT);

                view.setTextColor(textColor);
                view.setTextSize(day[0] == WEEKLABEL ? 10 : 12);
                view.setPadding((int)cellPadding, (int)rowPadding);

                return view;
            }
        };

        // Populate the adapter with the days of the month
        adapter.clear();
        for (int i = 1; i <= 7*7; i++) {
            adapter.add(new CalendarDayView(getContext()));
        }

        setAdapter(adapter);


        binding.monthGrid.setOnTouchListener((v, event)->{
            int action = event.getActionMasked();
            switch (action) {
                case MotionEvent.ACTION_UP:
                    /*
                    * Note: Calculate velocity after an ACTION_MOVE event,
                    *       not after ACTION_UP. After an ACTION_UP,
                    *       the X and Y velocities are 0.
                    * */
                    if (!inEditState) {
                        if (Math.abs(lastXVel) > Math.abs(lastYVel)) {
                            if (lastXVel > THRESHOLD)
                                leftSwipe();
                            else if (lastXVel < -THRESHOLD)
                                rightSwipe();
                        } else {
                            if (lastYVel > THRESHOLD)
                                leftSwipe();
                            else if (lastYVel < -THRESHOLD)
                                rightSwipe();
                        }

                        mVelocityTracker.clear();
                        lastXVel = 0; lastYVel = 0;
                    }
                    inEditState = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (inEditState) {
                        int position = binding.monthGrid.pointToPosition((int) event.getX(), (int) event.getY());
                        if (position != AdapterView.INVALID_POSITION) {
                            // Select the item at the touched position
                            int[] day = getDay(position);
                            if (day[0] >= 0) {
                                LocalDate pressedDate = LocalDate.of(
                                        focusedMoment.getYear(),
                                        focusedMoment.getMonthValue(),
                                        day[1]
                                );
                                setDate(pressedDate,
                                        false,
                                        editState,
                                        true
                                );
                            }
                        }
                        break;
                    }
                case MotionEvent.ACTION_DOWN:
                    if(mVelocityTracker == null) {
                        // Retrieve a new VelocityTracker object
                        mVelocityTracker = VelocityTracker.obtain();
                    }
                    else if (action == MotionEvent.ACTION_DOWN) {
                        // Reset the velocity tracker back to its initial state.
                        mVelocityTracker.clear();
                    }
                    // Add a user's movement to the tracker.
                    mVelocityTracker.addMovement(event);
                    getXVel();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    // Return a VelocityTracker object back to be re-used by others.
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                    break;
            }
            return false;
        });//*/

        refresh();
    }

    public void refresh() {
        adapter.notifyDataSetChanged();
    }

    private float getXVel() {
        mVelocityTracker.computeCurrentVelocity(1000);
        float xVel = mVelocityTracker.getXVelocity();
        float yVel = mVelocityTracker.getYVelocity();
        if (lastXVel == 0) {
            lastXVel = xVel;
            lastYVel = yVel;
        }
        return lastXVel;
    }

    public void setMonth(int year, int month) {
        //toValidMonth(year, month)
        this.focusedMoment = LocalDate.now().withYear(year).withMonth(month);
        this.month = month;
        this.year = year;
        this.dowOfMonthStart = focusedMoment.with(TemporalAdjusters.firstDayOfMonth()).getDayOfWeek().getValue();
        this.numDaysInPrevMonth = focusedMoment.with(TemporalAdjusters.firstDayOfMonth()).minusDays(1).getDayOfMonth();
        this.numDaysInThisMonth = focusedMoment.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        setMonthText(
                isInEditMode() ?
                        String.format(
                                Locale.getDefault(),
                                "%1$tY年%1$tm月",
                                focusedMoment
                        ).replace("年0","年")
                        : DateTimesFormatter.getYM(focusedMoment)
        );

        prevMonthInvalid = isInvalid(focusedMoment.withDayOfMonth(1).minusDays(1));
        binding.left.setColorFilter(prevMonthInvalid ? invalidColor : baseColor);
        binding.left.setOnClickListener((v)-> leftSwipe());

        nextMonthInvalid = isInvalid(focusedMoment.withDayOfMonth(1).plusMonths(2).minusDays(1));
        binding.right.setColorFilter(nextMonthInvalid ? invalidColor : baseColor);
        binding.right.setOnClickListener((v)-> rightSwipe());
    }

    public void setMonthText(String monthText) {
        binding.monthTitle.setText(monthText);
    }

    public void setBaseColor(Integer baseColor) {
        binding.monthTitle.setTextColor(baseColor);
        binding.left.setColorFilter(baseColor);
        binding.right.setColorFilter(baseColor);
    }

    public void setDate(LocalDate focus, boolean reset){ setDate(focus, reset, true, false); }
    public void setDate(LocalDate focus, boolean reset, boolean set, boolean noDeselect){
        focusedMoment = focus;
        if (multiSelectable) {
            if (set) {
                if (reset)
                    selected.clear();
                if (selected.contains(focus)) {
                    if (!noDeselect)
                        selected.remove(focus);
                } else
                    selected.add(focus);
                if (!noDeselect) editState = selected.contains(focus);
            } else if (noDeselect)
                selected.remove(focus);
        } else {
            selected.clear();
            selected.add(focus);
        }
        refresh();
        update(selected);
    }

    /** return format: [same_month, day, highlighted]
     *  same_month:
     *      -1: prev month
     *      0 : this month
     *      1 : next month */
    private int[] getDay(int position) {
        if (position < 7)
            return new int[]{WEEKLABEL, (position+6) % 7 + 1};

        position -= 7;
        int day = position-dowOfMonthStart+1;
        int month = focusedMoment.getMonthValue(), year = focusedMoment.getYear();
        int flag = day <= 0 ? PREVMONTH : day <= numDaysInThisMonth ? REGULAR : NEXTMONTH;

        if (flag == 0) {
            LocalDate theDay = LocalDate.of(focusedMoment.getYear(),focusedMoment.getMonth(),day);

            boolean isInvalid = isInvalid(theDay);

            if (selected.contains(theDay)) {
                if (isInvalid)
                    flag = DIFFMONTH_SUBHIGLIGHTED;
                else if (day == focusedMoment.getDayOfMonth())
                    flag = HIGHLIGHTED;
                else
                    flag = SUBHIGHLIGHTED;
            } else if (isInvalid)
                flag = DIFFMONTH_REGULAR;
        }
        else {
            LocalDate theDay = LocalDate.of(focusedMoment.getYear(),focusedMoment.getMonth(),1).plusDays(day-1);
            day = theDay.getDayOfMonth();
            month = theDay.getMonthValue();
            year = theDay.getYear();
            if (selected.contains(theDay))
                flag = DIFFMONTH_SUBHIGLIGHTED;
            else
                flag = DIFFMONTH_REGULAR;
        }
        return new int[]{flag, day, month, year};
    }

    public void leftSwipe() {
        if (prevMonthInvalid)
            return;
        if (month != 1) setMonth(year, month-1);
        else setMonth(year-1, 12);
        refresh();
    }

    public void rightSwipe() {
        if (nextMonthInvalid)
            return;
        if (month != 12) setMonth(year, month+1);
        else setMonth(year+1, 1);
        refresh();
    }

    public boolean selectedIsValid() {
        return selected.size() != 0;
    }

    private boolean isInvalid(LocalDate theDay) {
        return (minDateAllowed != null && theDay.isBefore(minDateAllowed))
                ||  (maxDateAllowed != null && theDay.isAfter(maxDateAllowed));
    }

    private LocalDate toValidMonth(int year, int month){
        LocalDate original = LocalDate.now().withYear(year).withMonth(month);
        LocalDate firstDayInMonth = original.withDayOfMonth(1);
        LocalDate lastDayInMonth = firstDayInMonth.plusMonths(1).minusDays(1);
        if (minDateAllowed != null && lastDayInMonth.isBefore(minDateAllowed))
            return minDateAllowed;
        if (maxDateAllowed != null && firstDayInMonth.isAfter(maxDateAllowed))
            return maxDateAllowed;
        return original;
    }

    private void setAdapter(ArrayAdapter<CalendarDayView> adapter) {
        binding.monthGrid.setAdapter(adapter);
        binding.monthGrid.setStretchMode(GridView.NO_STRETCH);
    }

    /** should only be called when widthMode is not set. */
    private void updateUI() {
        binding.monthGrid.setLayoutParams(
                new LinearLayout.LayoutParams(
                        widthMode == MeasureSpec.UNSPECIFIED ?
                                binding.monthGrid.getRequestedColumnWidth() * 7 :
                                widthSize,
                        LayoutParams.WRAP_CONTENT
                )
        );
        if (widthMode != MeasureSpec.UNSPECIFIED)
            binding.monthGrid.setColumnWidth(widthSize / 7);
    }

    private int widthMode = -1, widthSize;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        boolean toUpdate = widthMode == -1;

        widthMode = MeasureSpec.getMode(widthMeasureSpec);
        widthSize = MeasureSpec.getSize(widthMeasureSpec);

        if (toUpdate)
            updateUI();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public interface OnUpdateListener {
        void run(ArrayList<LocalDate> selected);
    }
}

interface Updatable {
    ArrayList<MultiDatePicker.OnUpdateListener> onUpdateListeners = new ArrayList<>();

    default void setOnUpdateListener(MultiDatePicker.OnUpdateListener onUpdateListener) {
        onUpdateListeners.add(onUpdateListener);
    }
    default void removeOnUpdateListener(MultiDatePicker.OnUpdateListener onUpdateListener) {
        onUpdateListeners.remove(onUpdateListener);
    }
    default void clearOnUpdateListeners() {
        onUpdateListeners.clear();
    }
    default void update(ArrayList<LocalDate> selected) {
        for (MultiDatePicker.OnUpdateListener onUpdateListener : onUpdateListeners)
            onUpdateListener.run(selected);
    }
}

interface Draggable {
    static boolean inBounds(View centerView, float x, float y, View view){
        x += centerView.getLeft();
        y += centerView.getTop();
        return (x >= view.getLeft() && x <= view.getRight() && y >= view.getTop() && y <= view.getBottom());
    }
}
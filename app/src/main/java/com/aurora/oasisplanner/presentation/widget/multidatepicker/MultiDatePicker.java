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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.databinding.MultiDatePickerBinding;
import com.aurora.oasisplanner.presentation.widget.multidatepicker.data.DateRange;
import com.aurora.oasisplanner.util.styling.DateTimesFormatter;
import com.aurora.oasisplanner.util.styling.Resources;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Locale;

public class MultiDatePicker extends UpdatableLinearLayout {
    private MultiDatePickerBinding binding;
    private LocalDate now = LocalDate.now();
    private LocalDate focusedMoment;
    private int dowOfMonthStart, numDaysInThisMonth, month, year;
    private ArrayAdapter<CalendarDayView> monthAdapter, weekAdapter;
    private boolean editState = true, inEditState = false;
    private boolean prevMonthInvalid, nextMonthInvalid;
    private VelocityTracker mVelocityTracker = null;
    private float lastXVel = 0, lastYVel = 0;
    private String[] weekDayName;
    private int dayOfWeek = 1;
    private boolean inWeekMode = false;
    private DatePickerMode selectionMode;
    private int dragItemId = 0;

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

    private ArrayList<LocalDate> selected = new ArrayList<>();
    private DateRange rangeSelected = new DateRange();
    public LocalDate minDateAllowed = null, maxDateAllowed = null;

    public MultiDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private static final float THRESHOLD = 0.01f;
    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context, AttributeSet attrs) {
        binding = MultiDatePickerBinding.inflate(LayoutInflater.from(context), this, true);

        dayOfWeek = now.getDayOfWeek().getValue();
        setMonth(now.getYear(), now.getMonthValue());
        selected.add(focusedMoment);
        rangeSelected.setPoint(focusedMoment);
        if (isInEditMode())
            selected.add(LocalDate.of(2023, 7, 16));

        // Attributes handling
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiDatePicker);

        // Selection Mode
        selectionMode = DatePickerMode.values()[a.getInt(R.styleable.MultiDatePicker_selectionMode, DatePickerMode.multiSelect.ordinal())];

        // Colors
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

        // Setting the general outlook of the date picker
        setBaseColor(baseColor);
        binding.monthTitle.setOnClickListener((v)->{
            dayOfWeek = now.getDayOfWeek().getValue();
            setInWeekMode(false);
            setMonth(now.getYear(), now.getMonthValue());
            refresh();
        });

        // Create an adapter for the GridView
        monthAdapter = new MonthViewAdapter(getContext(), 0);
        weekAdapter = new WeekViewAdapter(getContext(), 0);

        // Populate the adapter with the days of the month
        monthAdapter.clear();
        weekAdapter.clear();
        for (int i = 1; i <= 7*7; i++) {
            monthAdapter.add(new CalendarDayView(getContext()));
            weekAdapter.add(new CalendarDayView(getContext()));
        }

        setAdapter(inWeekMode ? weekAdapter : monthAdapter);

        binding.monthGrid.setOnTouchListener(this::onTouch);//*/

        refresh();
    }

    private boolean onTouch(View v, MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_UP:
                if (!inEditState)
                    handleSwipe();
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
                                    day[3],
                                    day[2],
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
                handleSwipeTouchDown(action, event);
                break;
            case MotionEvent.ACTION_CANCEL:
                // Return a VelocityTracker object back to be re-used by others.
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                break;
        }
        return false;
    }
    private void handleSwipe() {
        /*
         * Note: Calculate velocity after an ACTION_MOVE event,
         *       not after ACTION_UP. After an ACTION_UP,
         *       the X and Y velocities are 0.
         * */
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

        if (mVelocityTracker == null)
            mVelocityTracker = VelocityTracker.obtain();
        mVelocityTracker.clear();
        lastXVel = 0; lastYVel = 0;
    }
    private void handleSwipeTouchDown(int action, MotionEvent event) {
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
    }

    /** Retrieve the list of selected dates under the selectionMode 'multiSelect'*/
    public ArrayList<LocalDate> getMultiSelected() {
        assert selectionMode == DatePickerMode.multiSelect : "Selection Mode have to be 'multiSelect' to use the function 'getMultiSelected()'";
        return selected;
    }

    /** Retrieve the list of selected dates under the selectionMode 'singleSelect'*/
    public LocalDate getSingleSelected() {
        assert selectionMode == DatePickerMode.singleSelect : "Selection Mode have to be 'singleSelect' to use the function 'getSingleSelected()'";
        return selected.get(0);
    }

    /** Retrieve the start and end of the range selection under the selectionMode 'range'*/
    public DateRange getRangeSelected() {
        assert selectionMode == DatePickerMode.range : "Selection Mode have to be 'range' to use the function 'getRangeSelected()'";
        return rangeSelected;
    }

    private void weekLabelClicked(int dow) {
        dayOfWeek = dow;
        setInWeekMode(true);
        setMonth(year, month);
    }
    private void monthLabelClicked(int mo) {
        setInWeekMode(false);
        setMonth(year, mo);
    }

    private void setInWeekMode(boolean weekMode) {
        // Range mode cannot enter 'week mode'
        if (selectionMode == DatePickerMode.range)
            return;
        inWeekMode = weekMode;
        setAdapter(inWeekMode ? weekAdapter : monthAdapter);
        refresh();
    }

    public void refresh() {
        if (!inWeekMode)
            monthAdapter.notifyDataSetChanged();
        else
            weekAdapter.notifyDataSetChanged();
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
        if (inWeekMode) {
            focusedMoment = focusedMoment.with(TemporalAdjusters.firstDayOfMonth()).with(ChronoField.DAY_OF_WEEK, dayOfWeek);
            if (focusedMoment.getMonthValue() != month) focusedMoment = focusedMoment.plusWeeks(1);
        }
        this.month = month;
        this.year = year;
        this.dowOfMonthStart = focusedMoment.with(TemporalAdjusters.firstDayOfMonth()).getDayOfWeek().getValue();
        this.numDaysInThisMonth = focusedMoment.with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();

        if (!inWeekMode) {
            setMonthText(
                    isInEditMode() ?
                            String.format(
                                    Locale.getDefault(),
                                    "%1$tY年%1$tm月",
                                    focusedMoment
                            ).replace("年0","年")
                            : DateTimesFormatter.getYM(focusedMoment)
            );
        } else {
            setMonthText(
                    isInEditMode() ?
                            String.format(
                                    Locale.getDefault(),
                                    "%1$tY年"+(month>6?"下半年":"上半年")+" • %1$tA",
                                    focusedMoment
                            )
                            : DateTimesFormatter.getYW(focusedMoment)
                            .replace("$",
                                    month>6?
                                    Resources.getString(R.string.second_half_year):
                                    Resources.getString(R.string.first_half_year))
            );
        }

        prevMonthInvalid = isInvalid(focusedMoment.withDayOfMonth(1).minusDays(1));
        binding.left.setColorFilter(prevMonthInvalid ? invalidColor : baseColor);
        binding.left.setOnClickListener((v)-> leftSwipe());

        nextMonthInvalid = isInvalid(focusedMoment.withDayOfMonth(1).plusMonths(2).minusDays(1));
        binding.right.setColorFilter(nextMonthInvalid ? invalidColor : baseColor);
        binding.right.setOnClickListener((v)-> rightSwipe());
    }

    private void setMonthText(String monthText) {
        binding.monthTitle.setText(monthText);
    }

    private void setBaseColor(Integer baseColor) {
        binding.monthTitle.setTextColor(baseColor);
        binding.left.setColorFilter(baseColor);
        binding.right.setColorFilter(baseColor);
    }

    /** This function should not be used when in 'range' mode. */
    public void setFocus(LocalDate date) {
        setDate(date, false, true, true);
    }
    private void setDate(LocalDate focus, boolean reset){ setDate(focus, reset, true, false); }
    /**
     * This function should not be used when in 'range' mode except the case when reset is true.
     * @param reset true when this click clears previous selection
     * @param set true when in selection mode - and false when in deselection mode
     * @param noDeselect true when, for example, long clicking on focus (current selection) doesn't remove it
     * */
    private void setDate(LocalDate focus, boolean reset, boolean set, boolean noDeselect){
        focusedMoment = focus;
        switch (selectionMode) {
            case multiSelect:
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
                break;
            case singleSelect:
                selected.clear();
                selected.add(focus);
                break;
            case range:
                /*
                reset true, set true, noDeselect false: CLICKED
                reset false, set true, noDeselect false: PRESSED
                reset false, set true, noDeselect true: DRAGGED
                * */
                if (reset)
                    rangeSelected.setPoint(focus);
                else
                    rangeSelected.changeRangeBy(focus);
                break;
        }
        refresh();
        update(this);
    }

    /** returns the altered flag for the date (which is valid). */
    private int getFlagOfDay(LocalDate theDay, boolean isInvalid, int flag) {
        switch (selectionMode) {
            case multiSelect:
            case singleSelect:
                if (selected.contains(theDay)) {
                    if (isInvalid)
                        flag = DIFFMONTH_SUBHIGLIGHTED;
                    else if (theDay.equals(focusedMoment))
                        flag = HIGHLIGHTED;
                    else
                        flag = SUBHIGHLIGHTED;
                } else if (isInvalid)
                    flag = DIFFMONTH_REGULAR;
                break;
            case range:
                if (theDay.equals(rangeSelected.mStart) || theDay.equals(rangeSelected.mEnd))
                    flag = !isInvalid ? HIGHLIGHTED : DIFFMONTH_SUBHIGLIGHTED;
                else if (theDay.isAfter(rangeSelected.mStart) && theDay.isBefore(rangeSelected.mEnd))
                    flag = !isInvalid ? SUBHIGHLIGHTED : DIFFMONTH_SUBHIGLIGHTED;
                else if (isInvalid)
                    flag = DIFFMONTH_REGULAR;
                break;
        }
        return flag;
    }
    /** returns the altered flag for the date (which is invalid). */
    public int getFlagOfInvalidDay(LocalDate theDay, int flag) {
        switch (selectionMode) {
            case multiSelect:
            case singleSelect:
                if (selected.contains(theDay))
                    flag = DIFFMONTH_SUBHIGLIGHTED;
                else
                    flag = DIFFMONTH_REGULAR;
                break;
            case range:
                if (rangeSelected.contains(theDay))
                    flag = DIFFMONTH_SUBHIGLIGHTED;
                else
                    flag = DIFFMONTH_REGULAR;
                break;
        }
        return flag;
    }
    /** return format: [same_month, day, highlighted]
     *  same_month:
     *      -1: prev month
     *      0 : this month
     *      1 : next month */
    private int[] getDay(int position) {
        LocalDate theDay;
        boolean isInvalid;
        if (!inWeekMode) {
            if (position < 7)
                return new int[]{WEEKLABEL, (position + 6) % 7 + 1};

            position -= 7;
            int day = position-dowOfMonthStart+1;
            int month = focusedMoment.getMonthValue(), year = focusedMoment.getYear();
            int flag = day <= 0 ? PREVMONTH : day <= numDaysInThisMonth ? REGULAR : NEXTMONTH;

            if (flag == 0) {
                theDay = LocalDate.of(focusedMoment.getYear(),focusedMoment.getMonth(),day);

                isInvalid = isInvalid(theDay);

                flag = getFlagOfDay(theDay, isInvalid, flag);
            }
            else {
                theDay = LocalDate.of(focusedMoment.getYear(),focusedMoment.getMonth(),1).plusDays(day-1);
                day = theDay.getDayOfMonth();
                month = theDay.getMonthValue();
                year = theDay.getYear();

                flag = getFlagOfInvalidDay(theDay, flag);
            }
            return new int[]{flag, day, month, year};
        }
        else {
            int MONTH = position%7+((month>6 ? 5 : 0)), YEAR = focusedMoment.getYear();
            if (position < 7)
                return new int[]{WEEKLABEL, MONTH};

            position -= 7;
            theDay = LocalDate.of(YEAR,Month.of(MONTH+1),1);
            LocalDate d = theDay.with(ChronoField.DAY_OF_WEEK, dayOfWeek);
            theDay = d.isBefore(theDay) ? d.plusWeeks(1) : d;
            theDay = theDay.plusWeeks(position/7);
            int day = theDay.getDayOfMonth();
            int month = theDay.getMonthValue(), year = theDay.getYear();
            int flag = (month>MONTH+1 || year>YEAR) ? NEXTMONTH : REGULAR;

            isInvalid = isInvalid(theDay);
            if (flag == 0) {
                if (selected.contains(theDay)) {
                    if (isInvalid)
                        flag = DIFFMONTH_SUBHIGLIGHTED;
                    else
                        flag = HIGHLIGHTED;
                } else if (isInvalid)
                    flag = DIFFMONTH_REGULAR;
            }
            else {
                if (selected.contains(theDay))
                    flag = DIFFMONTH_SUBHIGLIGHTED;
                else
                    flag = DIFFMONTH_REGULAR;
            }
            return new int[]{flag, day, month, year};
        }
    }

    private void leftSwipe() {
        if (prevMonthInvalid)
            return;
        LocalDate prevMonth = focusedMoment.minusMonths(inWeekMode ? 6 : 1);
        setMonth(prevMonth.getYear(), prevMonth.getMonthValue());
        refresh();
    }

    private void rightSwipe() {
        if (nextMonthInvalid)
            return;
        LocalDate nextMonth = focusedMoment.plusMonths(inWeekMode ? 6 : 1);
        setMonth(nextMonth.getYear(), nextMonth.getMonthValue());
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

    public boolean[] getHasFrontEnd(LocalDate date) {
        switch (selectionMode) {
            case singleSelect:
            case multiSelect:
                return new boolean[]{
                        selected.contains(date.minusDays(1)),
                        selected.contains(date.plusDays(1))
                };
            case range:
                return new boolean[]{
                        rangeSelected.contains(date.minusDays(1)),
                        rangeSelected.contains(date.plusDays(1))
                };
        }
        return null;
    }

    class MonthViewAdapter extends ArrayAdapter<CalendarDayView> {

        public MonthViewAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }

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
                    boolean[] hasFrontEnd = getHasFrontEnd(date);
                    hasFront = hasFrontEnd[0];
                    hasEnd   = hasFrontEnd[1];
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
            } else {
                view.setOnClickListener(
                        (e) -> weekLabelClicked(day[1])
                );
                view.setEnabled(true);
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
    }

    class WeekViewAdapter extends ArrayAdapter<CalendarDayView> {

        public WeekViewAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }

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
                        (Month.values()[day[1]].getDisplayName(TextStyle.SHORT, Locale.getDefault()).toUpperCase(Locale.ROOT)+"")
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
                    if (!inWeekMode) {
                        hasFront = selected.contains(date.minusDays(1));
                        hasEnd = selected.contains(date.plusDays(1));
                    } else {
                        hasFront = hasEnd = false;
                    }
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
                } else {
                    view.setEnabled(false);
                }
            } else {
                int mo = day[1]+1;
                if (year < minDateAllowed.getYear() || (year == minDateAllowed.getYear() && mo < minDateAllowed.getMonthValue()))
                    view.setEnabled(false);
                else {
                    view.setOnClickListener(
                            (e) -> monthLabelClicked(mo)
                    );
                    view.setEnabled(true);
                }
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

    public interface OnUpdateListener {
        void run(MultiDatePicker picker);
    }

    /** The enum values must be identical to that defined inside attrs.xml */
    public enum DatePickerMode {
        multiSelect, singleSelect, range
    }
}

abstract class UpdatableLinearLayout extends LinearLayout {
    ArrayList<MultiDatePicker.OnUpdateListener> onUpdateListeners = new ArrayList<>();

    public UpdatableLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnUpdateListener(MultiDatePicker.OnUpdateListener onUpdateListener) {
        onUpdateListeners.add(onUpdateListener);
    }
    void removeOnUpdateListener(MultiDatePicker.OnUpdateListener onUpdateListener) {
        onUpdateListeners.remove(onUpdateListener);
    }
    void clearOnUpdateListeners() {
        onUpdateListeners.clear();
    }
    void update(MultiDatePicker picker) {
        for (MultiDatePicker.OnUpdateListener onUpdateListener : onUpdateListeners)
            onUpdateListener.run(picker);
    }
}

interface Draggable {
    static boolean inBounds(View centerView, float x, float y, View view){
        x += centerView.getLeft();
        y += centerView.getTop();
        return (x >= view.getLeft() && x <= view.getRight() && y >= view.getTop() && y <= view.getBottom());
    }
}
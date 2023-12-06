package com.aurora.oasisplanner.util.styling;

import android.util.Log;

import com.aurora.oasisplanner.R;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

public class DateTimesFormatter {
    private static final DateTimeFormatter
            timeFormat     = formatterOf(R.string.time_format),
            timeFormat12h  = formatterOf(R.string.time_format_12h),
            halfDateFormat = formatterOf(R.string.half_date_format),
            fullDateFormat = formatterOf(R.string.full_date_format),
            longDayFormat  = formatterOf(R.string.long_day_part),
            shortDayFormat = formatterOf(R.string.short_day_part),
            weekFormat     = formatterOf(R.string.week_format),
            weekRangeFormatStart_sameMonth = formatterOf(R.string.week_range_format_start_same_month),
            weekRangeFormatStart_diffMonth = formatterOf(R.string.week_range_format_start_different_month),
            weekRangeFormatEnd_sameMonth = formatterOf(R.string.week_range_format_end_same_month),
            weekRangeFormatEnd_diffMonth = formatterOf(R.string.week_range_format_end_different_month);
    private static final DateTimeFormatter[]
            partialDateFormats = formattersOf(R.string.partial_date_formats),
            homeDateFormats    = formattersOf(R.string.home_date_formats),
            barDateFormats     = formattersOf(R.string.bar_date_formats),
            monthFormats       = formattersOf(R.string.month_formats);
    public static ZoneOffset zoneOffset = ZoneOffset.ofHours(+8);
    private static final String SEP = ", "; private static final int SEPL = SEP.length();
    private static final String XML_SEP = ";";
    private static int year = -1;

    public static String getDateTime(List<LocalDate> dates, LocalTime start, LocalTime end) {
        try {
            if (dates.size() == 0) dates.add(LocalDate.now());
            return toDate(dates) + " " + getTime(start, end);
        } catch (Exception e) {
            return "#DATE#ERROR#";
        }
    }
    public static String getDateTime(List<LocalDate> dates, LocalTime time) {
        try {
            if (dates.size() == 0) dates.add(LocalDate.now());
            return toDate(dates) + " " + getTime(time);
        } catch (Exception e) {
            return "#DATE#ERROR#";
        }
    }
    public static String getTime(LocalTime start, LocalTime end) {
        if (start == null) start = LocalTime.now();
        return toTime(start) + (end == null ? "" : " ~ " + toTime(end));
    }
    public static String getTime(LocalTime start) {
        if (start == null) start = LocalTime.now();
        return toTime(start);
    }
    /*public Instant getItem(int i) {
        return dates.get(i).atStartOfDay().toInstant(zoneOffset).plusSeconds(time.toSecondOfDay());
    }//*/
    public static int getYear(){
        if (year == -1) year = LocalDate.now().getYear();
        return year;
    }
    public static String toTime(LocalTime t) {
        return t.format(timeFormat);
    }
    public static String toTime12h(LocalTime t) {
        return t.format(timeFormat12h);
    }
    public static String toDate(LocalDate d) {
        return d.format(d.getYear()==getYear() ? halfDateFormat : fullDateFormat);
    }
    public static String toDate(List<LocalDate> ds) {
        return toDate(ds, false, partialDateFormats);
    }
    public static String toDate(List<LocalDate> ds, boolean fullDate, DateTimeFormatter... formatters) {
        return toDate(ds, formatters[2], formatters[1], formatters[0], fullDate);
    }
    public static String toDate(List<LocalDate> ds,
                          DateTimeFormatter yearFormat,
                          DateTimeFormatter monthFormat,
                          DateTimeFormatter dayFormat, boolean fullDate) {
        try {
            if (ds.size() == 0) return "";

            ds.sort(LocalDate::compareTo);
            StringBuilder curY = new StringBuilder(),
                    curM = new StringBuilder();
            ArrayList<LocalDate> days = new ArrayList<>();
            LocalDate year = null, month = null;
            for (LocalDate d : ds) {
                if (year == null || d == null || year.getYear() != d.getYear()) {
                    if (year != null) {
                        curM.append(SEP).append(monthFormat.format(month).replace("_", daysConcat(days, dayFormat)));
                        curY.append(SEP).append(
                                ((year.getYear() == getYear() && !fullDate) ?
                                        "_" : yearFormat.format(year)).replace("_", sepSubstr(curM)));
                    }
                    curM = new StringBuilder(); year = d; month = null;
                }
                if (month == null || d == null || month.getMonth() != d.getMonth()) {
                    if (month != null)
                        curM.append(SEP).append(monthFormat.format(month).replace("_", daysConcat(days, dayFormat)));
                    month = d;
                    days.clear();
                }
                days.add(d);
            }
            curM.append(SEP).append(monthFormat.format(month).replace("_", daysConcat(days, dayFormat)));
            curY.append(SEP).append(
                    ((year.getYear()==getYear() && !fullDate) ? "_" : yearFormat.format(year))
                            .replace("_", sepSubstr(curM)));

            return sepSubstr(curY);
        } catch (Exception e) {
            return "#DATE#ERROR# ?"+e.getMessage()+"?";
        }
    }

    private static String daysConcat(ArrayList<LocalDate> days, DateTimeFormatter dayFormat) {
        boolean flag = false; LocalDate prev = null, start = null;
        int prevDate = -1;
        StringBuilder stringBuilder = new StringBuilder();
        if (days.size() == 0) return ""; // should not be executed, but should not crash program either.
        for (LocalDate d : days) {
            if (flag) {
                if (d.getDayOfMonth() != prevDate+1) {
                    stringBuilder.append(SEP).append(dayFormat.format(start));
                    if (!start.equals(prev))
                        stringBuilder.append(" - ").append(dayFormat.format(prev));
                    start = d;
                }
            } else {
                start = d;
                flag = true;
            }
            prev = d; prevDate = prev.getDayOfMonth();
        }
        stringBuilder.append(SEP).append(dayFormat.format(start));
        if (!Objects.equals(start, prev))
            stringBuilder.append(" - ").append(dayFormat.format(prev));
        return sepSubstr(stringBuilder);
    }

    private static String sepSubstr(StringBuilder sb){
        return sb.length() < SEPL ? "" : sb.substring(SEPL);
    }
    public static String getDay(LocalDate date){ return longDayFormat.format(date); }
    public static String getShortDay(LocalDate date){ return shortDayFormat.format(date); }
    public static String getYMW(LocalDate date){
        return toDate(Collections.singletonList(date), true, homeDateFormats);
    }
    public static String getWMD(LocalDate date){
        return toDate(Collections.singletonList(date), true, barDateFormats);
    }
    public static String getYM(LocalDate date){
        return toDate(Collections.singletonList(date), true, monthFormats);
    }
    public static String getWEEK(LocalDate date) { return weekFormat.format(date); }
    public static String getWeekRange(LocalDate date) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)),
                  endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));
        if (startOfWeek.getMonth() == endOfWeek.getMonth())
            return weekRangeFormatStart_sameMonth.format(startOfWeek)
                    + weekRangeFormatEnd_sameMonth.format(endOfWeek);
        return weekRangeFormatStart_diffMonth.format(startOfWeek)
                + weekRangeFormatEnd_diffMonth.format(endOfWeek);
    }

    private static DateTimeFormatter formatterOf(int formatterId) {
        return DateTimeFormatter.ofPattern(Resources.getString(formatterId));
    }
    private static DateTimeFormatter[] formattersOf(int formatterId) {
        String[] formatStrs = Resources.getString(formatterId).split(XML_SEP);
        DateTimeFormatter[] dtFormatters = new DateTimeFormatter[formatStrs.length];
        for (int i = 0 ; i < formatStrs.length ; i++)
            dtFormatters[i] = DateTimeFormatter.ofPattern(formatStrs[i]);
        return dtFormatters;
    }

    public static Date toDate(int year, int month, int day, int hour, int minute) {
        return new Date(new GregorianCalendar(year, month-1, day, hour, minute).getTimeInMillis());
    }
}

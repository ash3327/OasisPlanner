package com.aurora.oasisplanner.util.styling;

import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.DrawableRes;

import com.aurora.oasisplanner.R;

import java.util.Arrays;
import java.util.Objects;

public class Styles {
    public static String SEP = ";";

    public static SpannableStringBuilder toStyled(String htmlString) {
        SpannableStringBuilder output = new SpannableStringBuilder(Html.fromHtml(htmlString, Html.FROM_HTML_MODE_COMPACT));
        /* removing the trailing next line character generated by
        *  the <p> </p> tags during the Html.fromHtml method. */
        int len = output.length();
        if (len >= 1 && output.charAt(len - 1) == '\n')
            output.delete(len - 1, len);
        return output;
    }
    public static String toHtml(SpannableStringBuilder styledString) {
        return Html.toHtml(styledString, Html.FROM_HTML_MODE_COMPACT);
    }
    public static String toHtml(String pureString) {
        return Html.escapeHtml(pureString);
    }

    /*public static CharSequence toHtml(String styledString){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(
                    toHtmlStr(styledString),
                    HtmlCompat.FROM_HTML_MODE_COMPACT,
                    (source)->{
                        Drawable d = null;
                        switch (source) {
                            case "notif": d = Resources.getDrawable(R.drawable.menuic_notification); break;
                            case "agenda": d = Resources.getDrawable(R.drawable.menuic_calendar);     break;
                            case "deadline": d = Resources.getDrawable(R.drawable.ic_deadline);  break;
                            default: return null;
                        }
                        d.setBounds(0,0,
                                (int) Resources.getDimension(R.dimen.big_icon_height),
                                (int) Resources.getDimension(R.dimen.big_icon_height));
                        return d;
                    },
                    null
            );
        } else return styledString;
    }*/

    private static String toHtmlStr(String styledString) {
        return styledString.replace("\n", "<br>");
    }
    /** calculates the weighted length of a string.
     *  a chinese character is treated as 1 character,
     *  while every 3 English ascii characters are treated as 1 character.*/
    public static int getLength(String s){
        int sLen = s.length(),
                chinLen = s.replaceAll("[^\\p{L}]","")
                        .replaceAll("[A-Za-z]", "").length();
        return (sLen + chinLen) / 2;
    }
    public static String truncate(String str, int maxlen){
        return truncate(new SpannableStringBuilder(str), maxlen).toString();
    }
    public static SpannableStringBuilder truncate(SpannableStringBuilder str, int maxlen){
        String s = str.toString();
        if (getLength(s) < maxlen)
            return s.contains("\n") ? ((SpannableStringBuilder) str.subSequence(0, s.split("\n",2)[0].length())) : str;
        int i = 0, len = 0;
        while(len < (maxlen-1)*2 && i < str.length()) {
            if (str.charAt(i) == '\n') return ((SpannableStringBuilder) str.subSequence(0, i));
            if (str.charAt(i) < 256) len ++;
            else len += 2;
            i++;
        }
        return ((SpannableStringBuilder) str.subSequence(0, i-1)).append("...");
    }
    public static String strArrToStr(String[] arr) {
        return Arrays.stream(arr).reduce((a, b)->a+Styles.SEP+b).get();
    }
    public static String longArrToStr(long[] arr) {
        return Arrays.stream(arr)
                .mapToObj(Objects::toString)
                .reduce((a, b)->a+Styles.SEP+b).get();
    }
    public static String hashId(Object obj) {
        return Integer.toHexString(System.identityHashCode(obj));
    }
    public static int hashInt(Object obj) {
        return System.identityHashCode(obj);
    }

    public static boolean isEmpty(SpannableStringBuilder ssb) {
        if (ssb == null) return true;
        return ssb.toString().isEmpty();
    }
    public static boolean isEmpty(String s) {
        if (s == null) return true;
        return s.isEmpty();
    }

    public static void appendImageSpan(
            TextView tv, SpannableStringBuilder desc, @DrawableRes int drawableId) {
        int lineHei = tv.getLineHeight();
        Drawable tag = Resources.getDrawable(drawableId);
        tag.setBounds(0, 0, lineHei, lineHei);
        desc.append(" ", new ImageSpan(tag), 0);
    }

    /**
     * NOTE: PLEASE FOLLOW THE FOLLOWING FORMAT:
     *      [formatted text],[opt 1 for $1],[opt 2 for $1],[opt 1 for $2],...
     * Inside [formatted text], please write your formats in the following style:
     *      [other text]$[num] [other text]
     * *PLEASE REMEMBER TO ADD A SPACE AFTER THE $[num]. Also, indices starts from $1
     * */
    public static String substituteText(String text, boolean... options) {
        String[] sarr = text.split(",");
        String out = sarr[0];
        try {
            for (int i = sarr.length / 2 - 1; i >= 0; i--)
                out = out.replaceAll("\\$" + (i + 1) + " ", options[i] ? sarr[i * 2 + 1] : sarr[i * 2 + 2]);
        } catch (Exception e) { Log.e("StyleError (Internal)",Log.getStackTraceString(e.getCause())); }
        return out;
    }

    public static String loremIpsum() {
        return  "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris " +
                "nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in " +
                "reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. " +
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia " +
                "deserunt mollit anim id est laborum.";
    }
}

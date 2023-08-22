package com.aurora.oasisplanner.presentation.widget.multidatepicker;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;

import com.aurora.oasisplanner.databinding.MultiDatePickerCellBinding;

public class CalendarDayView extends LinearLayout {
    private MultiDatePickerCellBinding binding;
    private int day;

    public CalendarDayView(Context context) {
        super(context);
        init(context);
    }

    public CalendarDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        binding = MultiDatePickerCellBinding.inflate(LayoutInflater.from(context), this, true);

        // Set a default day number for the layout editor
        if (isInEditMode()) {
            setDay(1);
            setPadding(3, 2);
            setTextSize(12);
        }
    }

    public void setDay(int day) {
        this.day = day;
        setText(""+day);
    }

    public void setText(String str) {
        binding.text.setText(str);
    }

    public void setPadding(int padding, int outerPadding) {
        binding.framePanel.setPadding(padding, padding, padding, padding);
        binding.container.setPadding(0, outerPadding, 0, outerPadding);
    }

    public void setTheBackgroundColor(@ColorInt int color) {
        binding.container.setBackgroundColor(color);
    }
    public void setBackground(@ColorInt int color, boolean hasFront, boolean hasEnd) {
        GradientDrawable gradientDrawable = new GradientDrawable();
        int a = hasFront ? 0 : 100, b = hasEnd ? 0 : 100;
        gradientDrawable.mutate();
        gradientDrawable.setCornerRadii(new float[]{a, a, b, b, b, b, a, a});
        gradientDrawable.setColor(color);
        binding.framePanel.setBackground(gradientDrawable);
    }
    public void setForeground(@ColorInt int color) {
        binding.contentPanel.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    public void setTextColor(Integer color) {
        binding.text.setTextColor(color);
    }

    public void setTextSize(float size) {
        binding.text.setTextSize(size);
    }
}

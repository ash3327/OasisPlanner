package com.aurora.oasisplanner.presentation.ui.memos.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class InterceptLinearLayout extends LinearLayout {

    public InterceptLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(final MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_UP) {
            performClick();
            return true;
        }
        return false;
    }
}

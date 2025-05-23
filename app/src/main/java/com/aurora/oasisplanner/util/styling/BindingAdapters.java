package com.aurora.oasisplanner.util.styling;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.google.android.material.textfield.TextInputLayout;

public class BindingAdapters {
    @BindingAdapter("app:tint")
    public static void setTint(ImageView imageView, int color) {
        imageView.setColorFilter(color);
    }
}

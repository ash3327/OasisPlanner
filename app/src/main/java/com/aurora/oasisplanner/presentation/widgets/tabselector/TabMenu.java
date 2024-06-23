package com.aurora.oasisplanner.presentation.widgets.tabselector;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.databinding.TabMenuBinding;

import java.util.Arrays;
import java.util.List;

public class TabMenu extends LinearLayout{
    private TabMenuBinding binding;
    private List<MenuItem> menu;
    private int menuSize = 3, selectedId = 0;
    private OnChangeListener ocl;

    private Integer background;

    public TabMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        binding = TabMenuBinding.inflate(LayoutInflater.from(context), this, true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TabSelector);
        a.recycle();

        if (isInEditMode()) {
            createOptionMenu(
                    1,
                    Arrays.asList(
                            new MenuItem("Infos"),
                            new MenuItem("Dates"),
                            new MenuItem("Times")
                    ),
                    (i, menu, vbinding) -> {
                        if (!isInEditMode()) {
                            String[] colors = new String[]{
                                    "#FF0062EE", "#FF4A5A40", "#FFEE9337"
                            };
                            vbinding.selectContent.getBackground().setColorFilter(Color.parseColor(
                                    colors[i]
                            ), PorterDuff.Mode.SRC_IN);
                        }
                    }
            );
        }
    }

    /** THIS LINE MUST BE EXECUTED */
    public boolean createOptionMenu(int defaultId, List<MenuItem> menu, OnChangeListener ocl) {
        this.ocl = ocl;
        this.menu = menu;
        this.menuSize = menu.size();

        binding.backTabs.removeAllViews();
        binding.frontTabs.removeAllViews();

        for (int i = 0; i < menuSize ; i++) {
            if (i == defaultId)
                binding.backTabs.addView(binding.select);
            else
                binding.backTabs.addView(createBlankView());
            binding.frontTabs.addView(createView(i));
        }
        binding.container.post(()->onClick(defaultId));
        binding.notifyChange();
        return true;
    }
    private TextView createView(int i) {
        TextView view = createBlankView();
        view.setGravity(Gravity.CENTER);
        view.setTag(i);
        view.setText(menu.get(i).title);
        view.setOnClickListener((v)->onClick(i));
        return view;
    }
    private TextView createBlankView() {
        TextView view = new TextView(getContext());
        view.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                1.0f));
        return view;
    }

    public void onClick(int id) {
        try {
            int size = binding.select.getWidth();
            binding.select.animate().x(size * id).setDuration(100).start();
            selectedId = id;
            if (ocl != null)
                ocl.run(id, menu, binding);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public TabMenuBinding getBinding() {
        return binding;
    }

    public interface OnChangeListener {
        void run(int i, List<MenuItem> menu, TabMenuBinding binding);
    }

    public static class MenuItem {
        public String title;

        public MenuItem(String title) {
            this.title = title;
        }
    }
}

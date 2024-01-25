package com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.data.tags.ActivityType;

import java.util.Collections;

public abstract class _BaseAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    ItemTouchHelper itemTouchHelper = null;

    public abstract void remove(Object obj, int i);
    public abstract void insert(ActivityType.Type type, int i, String s);

    public abstract void swapItems(int fromPosition, int toPosition);
    public abstract void save();
    public abstract void removeChecked();
    public abstract void editTagOfChecked();
    public abstract void clearChecked();
    public abstract void checkAll();

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setDragHandle(View vi, RecyclerView.ViewHolder vh) {
        vi.setOnTouchListener((v,te)->{
            if (te.getActionMasked() == MotionEvent.ACTION_DOWN)
                itemTouchHelper.startDrag(vh);
            return true;
        });
    }

    public static void save(_BaseAdapter baseAdapter) {
        if (baseAdapter != null)
            baseAdapter.save();
    }
}

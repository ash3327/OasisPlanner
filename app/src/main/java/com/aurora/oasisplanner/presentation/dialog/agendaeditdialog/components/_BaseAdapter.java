package com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components;

import android.annotation.SuppressLint;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.data.util.Switch;
import com.aurora.oasisplanner.util.styling.Styles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class _BaseAdapter<T extends RecyclerView.ViewHolder, T2> extends RecyclerView.Adapter<T> {

    ItemTouchHelper itemTouchHelper = null;
    final Set<T2> checkedList;
    final Switch tSwitch;
    final OnSelectListener onSelectListener;

    public List<Object> items = new ArrayList<>();
    public List<ActivityType.Type> types = new ArrayList<>();

    {
        setHasStableIds(true);
    }

    public _BaseAdapter(OnSelectListener onSelectListener, Switch tSwitch) {
        this.checkedList = new HashSet<>();
        this.tSwitch = tSwitch;
        this.onSelectListener = onSelectListener;
    }

    // INFO: IMPLEMENTED METHODS
    @Override public int getItemCount() { return items.size(); }
    @Override public int getItemViewType(int position) { return types.get(position).ordinal(); }
    @Override
    public long getItemId(int position) {
        return (long) Styles.hashInt(items.get(position)) * types.size() + getItemViewType(position);
    }

    // INFO: ABSTRACT METHODS
    public abstract void remove(Object obj, int i);
    public abstract void insert(ActivityType.Type type, int i, String s);

    public abstract void swapItems(int fromPosition, int toPosition);
    public abstract void save();
    public abstract void removeChecked();
    public abstract void editTagOfChecked();
    abstract List<T2> getList();

    // INFO: UTIL METHODS
    public void clearChecked() {
        checkedList.clear();
        tSwitch.setState(false, true);
    }
    public void checkAll() {
        checkedList.addAll(getList());
        tSwitch.setState(true, true);
    }

    // INFO: CLASS METHODS
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

    public static void associate(EditText editText, SpannableStringBuilder text) {
        //prevents triggering at the initial change in text (initialization)
        Object tag = editText.getTag();
        if (tag instanceof TextWatcher)
            editText.removeTextChangedListener((TextWatcher) tag);

        editText.setText(text);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateUi() {
        checkedList.clear();
        tSwitch.setState(false, true);
        notifyDataSetChanged();
    }

    public void onUpdate(Set<T2> checkedList) {
        if (onSelectListener != null)
            onSelectListener.onSelect(checkedList.size()==getItemCount());
    }

    public interface OnSelectListener {void onSelect(boolean isFull);}

    // INFO: Base Holder
    abstract static class _BaseHolder<T4 extends RecyclerView.ViewHolder, T5, T3 extends _BaseAdapter<T4,T5>>
            extends RecyclerView.ViewHolder {
        ViewDataBinding vbinding;
        T3 adapter;
        final Set<T5> checkedList;
        /** aSwtich = true shows the checkboxes. */
        final Switch aSwitch;

        public _BaseHolder(ViewDataBinding binding, T3 adapter, Switch tSwitch, Set<T5> checkedList) {
            super(binding.getRoot());
            this.vbinding = binding;
            this.adapter = adapter;
            this.aSwitch = tSwitch;
            this.checkedList = checkedList;
        }

        public void checkToggle(T5 gp) {
            if (checkedList.contains(gp))
                checkedList.remove(gp);
            else
                checkedList.add(gp);
            aSwitch.setState(!checkedList.isEmpty(), true);
            adapter.onUpdate(checkedList);
        }
    }
}

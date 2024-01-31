package com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.entities.events.__Item;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.data.util.Switch;
import com.aurora.oasisplanner.util.styling.Styles;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class _BaseAdapter<T extends RecyclerView.ViewHolder, T2 extends __Item> extends RecyclerView.Adapter<T> {

    ItemTouchHelper itemTouchHelper = null;
    final Set<T2> checkedList;
    final Switch tSwitch;
    final OnSelectListener onSelectListener;

    public List<Object> items = new ArrayList<>();
    public List<ActivityType.Type> types = new ArrayList<>();
    long LId;
    RecyclerView recyclerView;

    boolean editable = false;

    {
        setHasStableIds(true);
    }

    public _BaseAdapter(OnSelectListener onSelectListener, RecyclerView recyclerView,
                        Switch tSwitch, boolean editable, long LId) {
        this.checkedList = new HashSet<>();
        this.recyclerView = recyclerView;
        this.tSwitch = tSwitch;
        this.onSelectListener = onSelectListener;
        this.editable = editable;
        this.LId = LId;
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
    public abstract void moveChecked();
    abstract List<T2> getList();

    public abstract void refreshDataset();

    // INFO: UTIL METHODS
    public void clearScroll() {
        LId = -1;
    }
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

    public static void associate(EditText editText, CharSequence text) {
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
    public void forceUpdateUi() {
        refreshDataset();
        updateUi();
    }

    public void onUpdate(Set<T2> checkedList) {
        if (onSelectListener != null)
            onSelectListener.onSelect(checkedList.size()==getItemCount());
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        clearScroll();
        refreshDataset();
    }
    public boolean isEditable() {
        return editable;
    }

    public interface OnSelectListener {void onSelect(boolean isFull);}

    // INFO: Base Holder
    abstract static class _BaseHolder<T4 extends RecyclerView.ViewHolder, T5 extends __Item, T3 extends _BaseAdapter<T4,T5>>
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

        public abstract void barOnClicked(int i, T5 gp, SpannableStringBuilder ssb);

        public void checkToggle(T5 gp) {
            if (checkedList.contains(gp))
                checkedList.remove(gp);
            else
                checkedList.add(gp);
            aSwitch.setState(!checkedList.isEmpty(), true);
            adapter.onUpdate(checkedList);
        }

        void bindDocText(EditText docTag, TextView docTagTv, T5 gp, int i, SpannableStringBuilder descr) {
            if (adapter.editable)
                bindDocEditText(docTag, docTagTv, gp, i, descr);
            else
                bindDocTextView(docTag, docTagTv, gp);
        }
        private void bindDocTextView(EditText docTag, TextView docTagTv, T5 gp) {
            docTag.setVisibility(View.GONE);
            docTagTv.setVisibility(View.VISIBLE);
            docTagTv.setText(gp.getTitle());
        }
        private void bindDocEditText(EditText docTag, TextView docTagTv, T5 gp, int i, SpannableStringBuilder descr) {
            docTagTv.setVisibility(View.GONE);
            docTag.setVisibility(View.VISIBLE);
            EditText docText = docTag;
            associate(docText, gp.getTitle());

            docText.setEnabled(true);
            docText.setFocusableInTouchMode(true);
            docText.setFocusable(true);

            docText.setOnClickListener(
                    (v)->{
                        docText.setFocusable(true);
                        barOnClicked(i, gp, descr);
                    }
            );//*/

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    docText.clearComposingText();
                    gp.setTitle(new SpannableStringBuilder(docText.getText()));
                }
            };
            docText.setTag(textWatcher);
            docText.addTextChangedListener(textWatcher);
        }
    }
}

package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components.viewargsbox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.tags.NotifType;
import com.aurora.oasisplanner.databinding.TagViewSubalarmDatetimeBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AEDDatetimeBox extends AEDBaseBox {
    private TagViewSubalarmDatetimeBinding binding;
    private OnChangeListener ocl;
    private ArrayList<NotifType> mNotifTypes = new ArrayList<>();
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;

    public AEDDatetimeBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {

    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void initBinding(Context context) {
        binding = TagViewSubalarmDatetimeBinding.inflate(LayoutInflater.from(context), this, true);
        mListView = binding.tagContentListview;
        mAdapter = new ArrayAdapter<>(
                context,
                R.layout.tag_view_subalarm_datetime_entry
        );
        mListView.setAdapter(mAdapter);
    }
    @Override
    protected TextView getTitleView() {
        return binding.tagTitleTv;
    }

    @Override
    protected ImageView getIcon() {
        return binding.icon;
    }

    @Override
    protected TextView getEditText() {
        return null;
    }

    protected TextView getTextView(int i) { return (TextView) binding.tagContentListview.getChildAt(i); }

    protected ImageView getAddRemoveButton() {
        return binding.btnAdd;
    }

    @Override
    protected View getChildContainer() {
        return binding.tagContentListview;
    }

    /** Adding and editing notif types and list items */
    public void addNotifType(NotifType notifType) {
        mNotifTypes.add(notifType);
        mAdapter.add(notifType.getDescription());
        mAdapter.notifyDataSetChanged();
        if (ocl != null)
            ocl.onChange(mNotifTypes);
    }
    public void setNotifType(NotifType notifType, int i) {
        mNotifTypes.set(i, notifType);
        getTextView(i).setText(notifType.getDescription());
        if (ocl != null)
            ocl.onChange(mNotifTypes);
    }
    public void setNotifTypes(List<NotifType> notifTypes) {
        mNotifTypes.clear();
        mNotifTypes.addAll(notifTypes);
        mAdapter.clear();
        mAdapter.addAll(notifTypes.stream().map(NotifType::getDescription).collect(Collectors.toList()));
    }
    /** This function cannot be used alone without using postRemovedNotifType at the end
     *  bc the count of highlighted items are NOT updated with this function. */
    public void removeNotifType(int i) {
        mNotifTypes.remove(i);
        mAdapter.remove(mAdapter.getItem(i));
    }
    public void postRemovedNotifType() {
        mAdapter.notifyDataSetChanged();
        if (ocl != null)
            ocl.onChange(mNotifTypes);
        removeAllHighlighting();
    }

    /** For list view item `v`. */
    public boolean getSelected(View v) {
        return v.getTag(R.id.selected_tag_key) != null;
    }
    public void setSelected(View v, boolean selected, boolean refreshButton) {
        boolean isNotSame = getSelected(v) != selected;
        v.setTag(R.id.selected_tag_key, selected ? true : null);
        v.setBackgroundResource(selected ? R.color.red_200 : 0);

        if (selected && mListView.getTag(R.id.selected_tag_key) == null)
            mListView.setTag(R.id.selected_tag_key, 1);
        else if (isNotSame)
            mListView.setTag(
                    R.id.selected_tag_key,
                    (int)mListView.getTag(R.id.selected_tag_key)+(selected?1:-1)
            );

        if (refreshButton)
            updateAddRemoveButton();
    }
    /** For list view itself */
    public boolean getHasSelected() {
        Object count = mListView.getTag(R.id.selected_tag_key);
        return count instanceof Integer && (int)count != 0;
    }
    public void removeAllHighlighting() {
        // remove all highlighting
        for (int j = 0; j < mListView.getChildCount(); j++) {
            View subview = mListView.getChildAt(j);
            setSelected(subview, false, false);
        }
        updateAddRemoveButton();
    }
    /** Add-Remove Button */
    private void updateAddRemoveButton() {
        boolean hasSelected = getHasSelected();
        getAddRemoveButton().setImageResource(
                hasSelected ?
                        R.drawable.ic_symb_trash :
                        R.drawable.ic_symb_plus
                );
    }

    /** On-change Listener */
    public void setOnChangeListener(OnChangeListener ocl) {
        this.ocl = ocl;
        mListView.setOnItemClickListener((p, v, pos, i)->{
            if (getHasSelected())
                removeAllHighlighting();
            else {
                // open up new dialog for input
                AppModule.retrieveEditEventUseCases().invokeDialogForSubalarm(
                        mNotifTypes.get((int) i),
                        (_notifType) -> setNotifType(_notifType, (int) i)
                );
            }
        });
        // Long click to select
        mListView.setOnItemLongClickListener((p, v, pos, i)->{
            // HASHCODE 0: SELECTED. SEE values/ids.xml for more.
            boolean selected = !getSelected(v);
            setSelected(v, selected, true);
            return true;
        });
        getAddRemoveButton().setOnClickListener((v)->{
            if (getHasSelected()) {
                for (int j = mListView.getChildCount()-1; j >= 0; j--) {
                    View child = mListView.getChildAt(j);
                    if (getSelected(child))
                        removeNotifType(j);
                }
                postRemovedNotifType();
            } else {
                removeAllHighlighting();
                addNotifType(NotifType.getDefault());
                setShowing(true);
            }
        });
    }

    public interface OnChangeListener {
        void onChange(List<NotifType> notifTypes);
    }
}

package com.aurora.oasisplanner.presentation.ui.memos.components;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities._Memo;
import com.aurora.oasisplanner.data.structure.Image;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.data.util.Id;
import com.aurora.oasisplanner.databinding.BoxEventBinding;
import com.aurora.oasisplanner.databinding.BoxMemoBinding;
import com.aurora.oasisplanner.databinding.BoxTaskBinding;
import com.aurora.oasisplanner.databinding.DayLabelBinding;
import com.aurora.oasisplanner.databinding.HeaderImageBinding;
import com.aurora.oasisplanner.presentation.ui.dividers.PaddingItemDecoration;
import com.aurora.oasisplanner.presentation.widget.taginputeidittext.TagInputEditText;
import com.aurora.oasisplanner.util.styling.DateTimesFormatter;
import com.aurora.oasisplanner.util.styling.Resources;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MemosAdapter extends RecyclerView.Adapter<MemosAdapter.MemosHolder> {

    private static final int ID_KEY_MEMOGROUPS = 0;
    private Id id;

    {
        setHasStableIds(true);
        id = new Id(-1, ID_KEY_MEMOGROUPS);
        id.observe((oldId, newId)->{
            if(oldId >= 0) notifyItemChanged(oldId);
            if(newId >= 0) notifyItemChanged(newId);
        });
    }

    public List<_Memo> memos = new ArrayList<>();

    public MemosAdapter(RecyclerView recyclerView) {
        if (!Objects.equals(recyclerView.getTag("hasDivider".hashCode()), true)) {
            recyclerView.addItemDecoration(
                    new PaddingItemDecoration(
                            R.dimen.paddingBoxesDecorationDefault,
                            R.dimen.paddingItemDecorationEdge
                    )
            );
            recyclerView.setTag("hasDivider".hashCode(), true);
        }
    }

    enum ViewType {Memo}
    @Override
    public int getItemViewType(int position) {
        return ViewType.Memo.ordinal();
    }

    @Override
    public long getItemId(int position) {
        _Memo item = memos.get(position);
        return item.id;
    }

    @NonNull
    @Override
    public MemosHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = BoxMemoBinding.inflate(li, parent, false); // null
        /*switch (ViewType.values()[viewType]) {
            case Memo:
                binding = BoxMemoBinding.inflate(li, parent, false);
                break;
        }//*/
        return new MemosHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MemosHolder holder, int position) {
        holder.bind(position, memos.get(position));
    }

    @Override
    public int getItemCount() {
        return memos.size();
    }

    /** Since the memo list will be overall changed when any memo is edited,
     *  a global memoication in change of ui is required. */
    @SuppressLint("MemoyDataSetChanged")
    public void setMemos(List<_Memo> memos) {

        this.memos.clear();
        this.memos.addAll(memos);
        if (onChangeListener != null)
            onChangeListener.run(memos.size());

        notifyDataSetChanged();
    }

    private OnChangeListener onChangeListener;
    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }

    static class MemosHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding vbinding;

        public MemosHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.vbinding = binding;
        }

        public boolean bind(int i, _Memo memo) {
            return bindMemo(i, memo);
        }

        public boolean bindMemo(int i, _Memo memo) {
            BoxMemoBinding binding = (BoxMemoBinding) vbinding;

            binding.bar.setOnClickListener(
                    (v)-> AppModule.retrieveMemoUseCases().editMemoUseCase.invoke(memo.id)
            );
            //if (memo.args.containsKey("importance"))
            //binding.triangle.getBackground().setColorFilter(, PorterDuff.Mode.SRC_OVER);
            binding.barTitle.setText(memo.title);
            binding.barDescriptionText.setText(memo.contents);
            binding.boxMemoTags.setInputType(InputType.TYPE_NULL);
            binding.boxMemoTags.setText(memo.getTagsString());  

            return true;
        }
    }

    public interface OnChangeListener {
        void run(int size);
    }
}

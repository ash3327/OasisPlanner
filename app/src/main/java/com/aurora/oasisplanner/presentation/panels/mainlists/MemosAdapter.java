package com.aurora.oasisplanner.presentation.panels.mainlists;

import android.annotation.SuppressLint;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities.memos._Memo;
import com.aurora.oasisplanner.presentation.util.Id;
import com.aurora.oasisplanner.databinding.BoxMemoBinding;
import com.aurora.oasisplanner.presentation.panels.utils.PaddingItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MemosAdapter extends RecyclerView.Adapter<MemosAdapter.MemosHolder> {

    private static final int ID_KEY_MEMOGROUPS = 1;
    private Id id;

    {
        setHasStableIds(true);
        id = new Id(-1, ID_KEY_MEMOGROUPS);//1
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

        @SuppressLint("ClickableViewAccessibility")
        public boolean bindMemo(int i, _Memo memo) {
            BoxMemoBinding binding = (BoxMemoBinding) vbinding;

            View.OnClickListener ocl = (v)-> AppModule.retrieveMemoUseCases().edit(memo.id);

            binding.bar.setOnClickListener(ocl);
            //if (memo.args.containsKey("importance"))
            //binding.triangle.getBackground().setColorFilter(, PorterDuff.Mode.SRC_OVER);
            binding.barTitle.setText(memo.title);
            binding.barDescriptionText.setText(memo.contents);
            String s = memo.getTagsString();

            binding.boxMemoTags.setInputType(InputType.TYPE_NULL);
            if (s == null)
                binding.boxMemoTagsScroll.setVisibility(View.GONE);
            else {
                binding.boxMemoTagsScroll.setVisibility(View.VISIBLE);
                binding.boxMemoTags.setTags(s);
            }

            return true;
        }
    }

    public interface OnChangeListener {
        void run(int size);
    }

}

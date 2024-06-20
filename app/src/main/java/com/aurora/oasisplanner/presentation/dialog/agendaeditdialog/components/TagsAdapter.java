package com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.data.tags.NotifType;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.databinding.ItemTagContentBinding;
import com.aurora.oasisplanner.databinding.ItemTagNotifBinding;
import com.aurora.oasisplanner.databinding.ItemTagTagsBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TagsAdapter extends RecyclerView.Adapter<TagsAdapter.TagHolder> {

    public List<Map.Entry<String, String>> entries;

    @Override
    public int getItemViewType(int position) {
        return TagType.valueOf(entries.get(position).getKey()).ordinal();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public TagHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewDataBinding binding = null;
        LayoutInflater li = LayoutInflater.from(parent.getContext());
        switch (TagType.values()[viewType]) {
            case LOC:
            case DESCR:
                binding = ItemTagContentBinding.inflate(li, parent, false);
                break;
            case ALARM:
                binding = ItemTagNotifBinding.inflate(li, parent, false);
                break;
            case TAGS:
                binding = ItemTagTagsBinding.inflate(li, parent, false);
                break;
        }
        return new TagHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TagHolder holder, int position) {
        Map.Entry<String,String> entry = entries.get(position);
        holder.bind(position, TagType.valueOf(entry.getKey()), entry.getValue());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    /** Since the alarm list will be overall changed when any agenda is edited,
     *  a global notification in change of ui is required. */

    @SuppressLint("NotifyDataSetChanged")
    public void setTags(Map<String, String> map, List<TagType> exclude) {
        if (map == null) map = new HashMap<>();
        Set<Map.Entry<String,String>> entrySet = map.entrySet();
        entrySet = new HashSet<>(entrySet);
        entrySet.removeIf((e)->!TagType.contains(e.getKey())||exclude.contains(TagType.valueOf(e.getKey())));
        this.entries = new ArrayList<>(entrySet);
        notifyDataSetChanged();
    }

    static class TagHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding vbinding;

        public TagHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.vbinding = binding;
        }

        public boolean bind(int i, TagType key, String val) {
            switch (key) {
                case LOC:
                case DESCR:
                    return bindTv(i, key, val);
                case ALARM:
                    return bindNotif(i, key, val);
                case TAGS:
                    return bindTags(i, key, val);
            }
            return false;
        }

        public boolean bindTv(int i, TagType key, String val) {
            ItemTagContentBinding binding = (ItemTagContentBinding) vbinding;
            binding.itemTagIcon.setImageDrawable(key.getSmallDrawable());
            binding.itemTagText.setText(new Converters().spannableFromString(val));
            return true;
        }

        public boolean bindNotif(int i, TagType key, String val) {
            val = new Converters().spannableFromString(val).toString();

            ItemTagNotifBinding binding = (ItemTagNotifBinding) vbinding;
            binding.itemTagIcon.setImageDrawable(key.getSmallDrawable());
            binding.itemTagText.setText(NotifType.loadDescFrom(NotifType.loadFromString(val)));
            return true;
        }

        public boolean bindTags(int i, TagType key, String val) {
            assert val != null;
            ItemTagTagsBinding binding = (ItemTagTagsBinding) vbinding;
            binding.itemTagIcon.setImageDrawable(key.getSmallDrawable());
            binding.boxMemoTags.setText(new Converters().spannableFromString(val).toString());
            return true;
        }
    }
}

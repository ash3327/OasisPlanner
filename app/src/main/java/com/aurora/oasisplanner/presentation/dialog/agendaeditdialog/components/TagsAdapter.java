package com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.model.entities._Activity;
import com.aurora.oasisplanner.data.model.pojo.Agenda;
import com.aurora.oasisplanner.data.model.pojo.Activity;
import com.aurora.oasisplanner.data.model.entities._Doc;
import com.aurora.oasisplanner.data.model.pojo.AlarmList;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.data.util.Id;
import com.aurora.oasisplanner.data.util.Switch;
import com.aurora.oasisplanner.databinding.ItemTagContentBinding;
import com.aurora.oasisplanner.databinding.SectionBinding;
import com.aurora.oasisplanner.databinding.SectionDocBinding;
import com.aurora.oasisplanner.databinding.SectionGapBinding;
import com.aurora.oasisplanner.util.styling.DateTimesFormatter;
import com.aurora.oasisplanner.util.styling.Resources;
import com.aurora.oasisplanner.util.styling.Styles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

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
                binding = ItemTagContentBinding.inflate(li, parent, false);
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
    public void setTags(Map<String, String> map) {
        Set<Map.Entry<String,String>> entrySet = map.entrySet();
        entrySet.removeIf((e)->!TagType.contains(e.getKey()));
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
                    bindTv(i, key, val);
            }
            return false;
        }

        public boolean bindTv(int i, TagType key, String val) {
            ItemTagContentBinding binding = (ItemTagContentBinding) vbinding;
            binding.itemTagIcon.setImageDrawable(key.getDrawable());
            binding.itemTagText.setText(new Converters().spannableFromString(val));
            return true;
        }
    }
}
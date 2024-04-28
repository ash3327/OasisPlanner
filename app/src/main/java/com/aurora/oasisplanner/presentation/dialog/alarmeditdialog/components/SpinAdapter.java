package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.databinding.TagTypeSpinnerElementBinding;

public class SpinAdapter extends ArrayAdapter<TagType> {
    private LayoutInflater li;
    private TagType[] vals;

    public SpinAdapter(LayoutInflater li, @NonNull TagType[] typeList) {
        super(li.getContext(), R.layout.tagtype_spinner_element);
        this.li = li;
        this.vals = typeList;
    }

    @Override
    public int getCount() {
        return vals.length;
    }

    @Override
    public TagType getItem(int position) {
        return vals[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getDaView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getDaView(position, convertView, parent);
    }

    public View getDaView(int position, View convertView, ViewGroup parent) {
        TagTypeSpinnerElementBinding binding = TagTypeSpinnerElementBinding.inflate(li);
        TagType type = getItem(position);
        binding.text.setText(type.toString());
        binding.icon.setImageDrawable(type.getDrawable());
        return binding.getRoot();
    }
}

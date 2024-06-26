package com.aurora.oasisplanner.data.core.use_cases;

import android.graphics.Color;

import androidx.annotation.ColorInt;

import com.aurora.oasisplanner.data.model.entities.util._Tag;
import com.aurora.oasisplanner.data.repository.TagRepository;

public class GetTagUseCases {
    private TagRepository repository;

    public GetTagUseCases(TagRepository repository) {
        this.repository = repository;
    }

    public _Tag get(String name) {

        @ColorInt int col = _Tag.defaultCol;
        if (name.contains(":")) {
            String[] sarr = name.split(":");
            try {
                col = Color.parseColor(sarr[1]);
            } catch (Exception e) {}
            name = sarr[0];
        }
        _Tag t = repository.getTagFromName(name);
        if (t == null) {
            t = new _Tag();
            t.name = name;
            t.color = col;
            t.isNew = true;
        }
        if (t.color != col && col != _Tag.defaultCol) {
            t.color = col;
            t.isNew = true;
        }
        return t;
    }

    public void put(_Tag tag) {
        repository.insert(tag);
    }
}

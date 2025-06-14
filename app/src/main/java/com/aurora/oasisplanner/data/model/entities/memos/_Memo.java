package com.aurora.oasisplanner.data.model.entities.memos;

import android.text.SpannableStringBuilder;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.presentation.widgets.taginputeidittext.TagInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
public class _Memo {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public SpannableStringBuilder title;
    public SpannableStringBuilder contents;
    public Map<String,String> args = new HashMap<>();
    public List<String> tags;

    @Ignore
    public boolean visible = true;

    public _Memo() {}

    @Ignore
    public _Memo(CharSequence title, CharSequence contents) {
        this.title = new SpannableStringBuilder(title);
        this.contents = new SpannableStringBuilder(contents);
    }

    @Ignore
    public static _Memo empty() {
        return new _Memo("", "");
    }

    @Ignore
    public SpannableStringBuilder getArg(String key) {
        if (args == null || !args.containsKey(key))
            return null;
        return new Converters().spannableFromString(args.get(key));
    }

    @Ignore
    public void putTags(List<String> tags) {
        if (this.tags == null)
            this.tags = new ArrayList<>();
        this.tags.addAll(tags);
    }

    @Ignore
    public List<String> getTags() {
        if (this.tags == null)
            this.tags = new ArrayList<>();
        return this.tags;
    }

    @Ignore
    public String getTagsString() {
        return Objects.requireNonNull(getTags()).stream()
                .reduce("", (a, b)->a.isEmpty()?b:a+ TagInputEditText.SEP+b)+TagInputEditText.SEP;
    }

    @Ignore
    public String toString() {
        return " [_Memo : "+id+" : "+title+","+contents+"] ";
    }
}

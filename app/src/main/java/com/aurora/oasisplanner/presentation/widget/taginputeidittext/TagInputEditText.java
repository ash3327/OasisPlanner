package com.aurora.oasisplanner.presentation.widget.taginputeidittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aurora.oasisplanner.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.text.MessageFormat;
import java.util.Objects;

public class TagInputEditText extends TextInputEditText {
    TextWatcher textWatcher;
    String lastString = null;
    public static final String SEP = " ";
    public boolean editable = true;

    public TagInputEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TagInputEditText,
                0, 0);
        try {
            editable = a.getBoolean(R.styleable.TagInputEditText_editable, true);
        } finally {
            a.recycle();
        }
        init();
    }

    private void init() {
        setMovementMethod(LinkMovementMethod.getInstance());

        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (str.length() > 0 && !str.equals(lastString))
                    format();
            }
        };

        addTextChangedListener(textWatcher);
    }

    private void format() {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        String fullString = Objects.requireNonNull(getText()).toString();

        String[] strings = fullString.split(SEP);
        int fullStringLastCh = fullString.length()-1;

        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            stringBuilder.append(s);

            if (fullString.charAt(fullStringLastCh) != SEP.charAt(0) && i == strings.length-1)
                break;

            BitmapDrawable bitmapDrawable = (BitmapDrawable) convertViewToDrawable(createTokenView(s));
            bitmapDrawable.setBounds(0, 0, bitmapDrawable.getIntrinsicWidth(), bitmapDrawable.getIntrinsicHeight());

            int startIdx = stringBuilder.length() - s.length();
            int endIdx = stringBuilder.length();

            CLickableSpan span = new CLickableSpan(startIdx, endIdx);
            stringBuilder.setSpan(span, Math.max(endIdx-2, startIdx), endIdx, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            stringBuilder.setSpan(new ImageSpan(bitmapDrawable), startIdx, endIdx, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            if (i < strings.length-1)
                stringBuilder.append(SEP);
            else if (fullString.charAt(fullStringLastCh) == SEP.charAt(0))
                stringBuilder.append(SEP);
        }
        lastString = stringBuilder.toString();

        setText(stringBuilder);
        setSelection(stringBuilder.length());
    }

    public View createTokenView(String text) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.tags_token_layout, layout, false);

        Chip chip = view.findViewById(R.id.chip);
        chip.setCloseIconVisible(editable);
        chip.setText(text);
        layout.addView(chip);

        return layout;
    }

    public Object convertViewToDrawable(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight()*3/5, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.translate(-view.getScrollX(), -view.getScrollY()-view.getMeasuredHeight()/5f);
        view.draw(canvas);

        return new BitmapDrawable(getContext().getResources(), bitmap);
    }

    private class CLickableSpan extends android.text.style.ClickableSpan {
        int startIdx;
        int endIdx;

        public CLickableSpan(int startIdx, int endIdx) {
            super();
            this.startIdx = startIdx;
            this.endIdx = endIdx;
        }

        @Override
        public void onClick(@NonNull View widget) {
            if (!editable) return;
            String s = Objects.requireNonNull(getText()).toString();
            String s1 = s.substring(0, startIdx);
            String s2 = s.substring(Math.min(endIdx+1, s.length()-1));
            TagInputEditText.this.setText(MessageFormat.format("{0}{1}", s1, s2));
        }
    }
}

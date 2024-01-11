package com.aurora.oasisplanner.presentation.widget.taginputeidittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.core.use_cases.general_usecases.GetTagUseCase;
import com.aurora.oasisplanner.data.model.entities._Tag;
import com.aurora.oasisplanner.util.styling.Resources;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

public class TagInputEditText extends TextInputEditText {
    TextWatcher textWatcher;
    String lastString = null;
    public static final String SEP = " ";
    public boolean editable;

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

    public static String format(String str) {
        String[] strings = str.trim().replaceAll("\\s+"," ").split(SEP);

        HashSet<String> set = new HashSet<>(Arrays.asList(strings));
        return set.stream().reduce(SEP, (a,b)->a+SEP+b).trim()+SEP;
    }
    public static SpannableStringBuilder combine(
            SpannableStringBuilder a, SpannableStringBuilder b
    ) {
        if (a == null) a = new SpannableStringBuilder();
        if (b == null) b = new SpannableStringBuilder();
        String c = a+SEP+b;
        return new SpannableStringBuilder(format(c));
    }

    private void format() {
        int selection = getSelectionStart();
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        String fullString = Objects.requireNonNull(getText()).toString();

        String[] strings = fullString.trim().replaceAll("\\s+"," ").split(SEP);
        int fullStringLastCh = fullString.length()-1;

        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            stringBuilder.append(s);

            if (fullString.charAt(fullStringLastCh) != SEP.charAt(0) && i == strings.length-1)
                break;

            int startIdx = stringBuilder.length() - s.length();
            int endIdx = stringBuilder.length();

            View tokenView = createTokenView(s);
            setTokenView(tokenView, startIdx, endIdx, stringBuilder);

            if (editable) {
                ClickableSpan span = new ClickableSpan(startIdx, endIdx, tokenView);
                stringBuilder.setSpan(span, startIdx, endIdx, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                            //Math.max(endIdx - 2, startIdx)
            }

            if (i < strings.length-1)
                stringBuilder.append(SEP);
            else if (fullString.charAt(fullStringLastCh) == SEP.charAt(0))
                stringBuilder.append(SEP);
        }
        lastString = stringBuilder.toString();

        setText(stringBuilder);
        setSelection(Math.min(stringBuilder.length(), selection));
    }

    public View createTokenView(String text) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);

        View view = LayoutInflater.from(getContext()).inflate(R.layout.tags_token_layout, layout, false);

        GetTagUseCase getTagUseCase = AppModule.retrieveGeneralUseCases().getTagUseCase;
        _Tag t = getTagUseCase.invoke(text);
        view.findViewById(R.id.tag_chip).getBackground().setColorFilter(
                new PorterDuffColorFilter(t.color, PorterDuff.Mode.SRC_OVER)
        );

        TextView tv = view.findViewById(R.id.tag_tv);
        tv.setText(t.name);
        tv.setTextColor(t.getTextColor());

        ImageView imgv = view.findViewById(R.id.tag_cross);
        imgv.setVisibility(editable ? VISIBLE : GONE);
        view.setFocusable(editable);
        layout.addView(view);

        if (t.isNew)
            getTagUseCase.put(t);

        return layout;
    }

    public Object convertViewToDrawable(View view) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        /*
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight()*3/5, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.translate(-view.getScrollX(), -view.getScrollY()-view.getMeasuredHeight()/5f);
        view.draw(canvas);
        */
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(canvas);

        return new BitmapDrawable(getContext().getResources(), bitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        float px = me.getX(), py = me.getY();
        float x = px-getPaddingStart(), y = py-getPaddingTop();

        Layout layout = getLayout();

        int offset = getOffsetForPosition(px, py);

        if (me.getAction() == MotionEvent.ACTION_UP) {
            try {
                Spannable spannable = Objects.requireNonNull(getText());
                ClickableSpan[] link = spannable.getSpans(offset, offset, ClickableSpan.class);
                if (link.length != 0) {
                    link[0].onClick(x,y,layout);

                    return true;
                }
            } finally {
                //
            }
        }
        super.onTouchEvent(me);

        return true;
    }

    private class ClickableSpan extends android.text.style.ClickableSpan {
        int startIdx;
        int endIdx;
        View tokenView;

        public ClickableSpan(int startIdx, int endIdx, View tokenView) {
            super();
            this.startIdx = startIdx;
            this.endIdx = endIdx;
            this.tokenView = tokenView;
        }

        @Override
        public void onClick(@NonNull View widget) {
            //if (!editable) return;
            //removeText(startIdx, endIdx);
        }

        public void onClick(float x, float y, Layout layout) {
            if (!editable) return;

            if (inBounds(getDelBtnBounds(layout), x, y)) {
                removeText(startIdx, endIdx);
                return;
            }
            editColorOfTag(tokenView, startIdx, endIdx);
        }

        public Rect getBounds(Layout layout) {
            Rect bounds = new Rect();
            int line = layout.getLineForOffset(startIdx);
            bounds.top = layout.getLineTop(line);
            bounds.bottom = layout.getLineBottom(line);
            bounds.left = (int) layout.getPrimaryHorizontal(startIdx);
            bounds.right = (int) layout.getPrimaryHorizontal(endIdx);
            return bounds;
        }
        public Rect getDelBtnBounds(Layout layout) {
            Rect bounds = getBounds(layout);
            ImageView delBtn = tokenView.findViewById(R.id.tag_cross);
            bounds.left += delBtn.getX();
            return bounds;
        }
        public boolean inBounds(Rect bounds, float x, float y) {
            return (x >= bounds.left) && (x <= bounds.right)
                    && (y >= bounds.top) && (y <= bounds.bottom);
        }
    }

    private void removeText(int startIdx, int endIdx) {
        String s = Objects.requireNonNull(getText()).toString();
        String s1 = s.substring(0, startIdx);
        String s2 = s.substring(Math.min(endIdx+1, s.length()-1));
        TagInputEditText.this.setText(MessageFormat.format("{0}{1}", s1, s2));
    }

    private void editColorOfTag(View tokenView, int startIdx, int endIdx) {
        LinearLayout token = tokenView.findViewById(R.id.tag_chip);
        token.getBackground().setColorFilter(
                new PorterDuffColorFilter(Resources.getColor(R.color.red_100), PorterDuff.Mode.SRC_ATOP)
        );
        SpannableStringBuilder ssb = new SpannableStringBuilder(getText());
        ssb = setTokenView(tokenView, startIdx, endIdx, ssb);
        setText(ssb);
    }

    private SpannableStringBuilder setTokenView(View tokenView, int startIdx, int endIdx,
                                               SpannableStringBuilder stringBuilder) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) convertViewToDrawable(tokenView);
        bitmapDrawable.setBounds(0, 0, bitmapDrawable.getIntrinsicWidth(), bitmapDrawable.getIntrinsicHeight());

        stringBuilder.setSpan(new ImageSpan(bitmapDrawable), startIdx, endIdx, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return stringBuilder;
    }

    public void setTags(String text) {
        lastString = null;
        setText(text);
    }
}

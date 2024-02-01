package com.aurora.oasisplanner.presentation.widget;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.databinding.AddItemEditTextBinding;
import com.aurora.oasisplanner.util.styling.Resources;
import com.aurora.oasisplanner.util.styling.Styles;

public class AddItemEditText extends LinearLayout {
    private final ImageView icon;
    private final EditText editText;
    private final AddItemEditTextBinding binding;
    private final View root;
    private final TextView warningTv;

    public AddItemEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = AddItemEditTextBinding.inflate(inflater, this, true);
        icon = binding.aietIcon;
        editText = binding.aietEditText;
        warningTv = binding.aietEditWarning;
        root = binding.getRoot();
    }

    public ImageView getIcon() { return icon; }
    public EditText getEditText() { return editText; }

    public @StringRes int inputWarnings(CharSequence s) {
        if (Styles.getLength(s.toString()) > 13)
            return R.string.warning_input_too_long;
        return -1;
    }
    public void setOnEnterListener(OnEnterListener r) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                @StringRes int warnings = inputWarnings(s);
                if (warnings != -1) {
                    root.setBackgroundColor(Resources.getColor(R.color.warning_color));
                    warningTv.setVisibility(VISIBLE);
                    warningTv.setText(warnings);
                } else {
                    root.setBackgroundColor(Color.TRANSPARENT);
                    warningTv.setVisibility(GONE);
                }
            }
        });
        editText.setOnKeyListener((view,keyCode,keyEvent)->{
            String s = editText.getText().toString().trim();

            if (keyCode == KeyEvent.KEYCODE_ENTER &&
                    keyEvent.getAction() == KeyEvent.ACTION_UP
            ) {
                if (!s.isEmpty())
                    r.run(s);
                editText.setText("");
                return true; // Consume key event.
            }
            return false; // Not consumed event
        });
    }

    public void setEditable(boolean editable) {
        editText.setEnabled(editable);
        binding.getRoot().setVisibility(editable ? VISIBLE : INVISIBLE);
    }

    public interface OnEnterListener {
        void run(String input);
    }
}

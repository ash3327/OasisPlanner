package com.aurora.oasisplanner.presentation.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.aurora.oasisplanner.databinding.AddItemEditTextBinding;

public class AddItemEditText extends LinearLayout {
    private ImageView icon;
    private EditText editText;

    public AddItemEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        AddItemEditTextBinding binding = AddItemEditTextBinding.inflate(inflater, this, true);
        icon = binding.aietIcon;
        editText = binding.aietEditText;
    }

    public ImageView getIcon() { return icon; }
    public EditText getEditText() { return editText; }

    public void setOnEnterListener(OnEnterListener r) {
        editText.setOnKeyListener((view,keyCode,keyEvent)->{
            if (keyCode == KeyEvent.KEYCODE_ENTER &&
                    keyEvent.getAction() == KeyEvent.ACTION_UP) {
                r.run(editText.getText().toString().trim());
                editText.setText("");
                return true; // Consume key event.
            }
            return false; // Not consumed event
        });
    }

    public interface OnEnterListener {
        void run(String input);
    }
}

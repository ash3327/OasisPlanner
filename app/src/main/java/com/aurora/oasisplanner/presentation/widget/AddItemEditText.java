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
    private final ImageView icon;
    private final EditText editText;
    private final AddItemEditTextBinding binding;

    public AddItemEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = AddItemEditTextBinding.inflate(inflater, this, true);
        icon = binding.aietIcon;
        editText = binding.aietEditText;
    }

    public ImageView getIcon() { return icon; }
    public EditText getEditText() { return editText; }

    public void setOnEnterListener(OnEnterListener r) {
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

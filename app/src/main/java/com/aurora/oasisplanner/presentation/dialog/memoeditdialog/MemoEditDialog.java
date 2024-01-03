package com.aurora.oasisplanner.presentation.dialog.memoeditdialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities._Memo;
import com.aurora.oasisplanner.databinding.MemoPageBinding;
import com.aurora.oasisplanner.presentation.widget.taginputeidittext.TagInputEditText;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.Objects;

public class MemoEditDialog extends AppCompatDialogFragment {
    public static final String EXTRA_MEMO_ID = "memoId";

    private _Memo memo;
    private AlertDialog dialog;
    private MemoPageBinding vbinding;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert getArguments() != null;

        long memoId = getArguments().getLong(EXTRA_MEMO_ID, -1);
        if (memoId != -1)
            memo = AppModule.retrieveMemoUseCases().getMemoUseCase.invoke(memoId);
        else
            memo = _Memo.empty();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        MemoPageBinding binding = MemoPageBinding.inflate(getLayoutInflater());
        onBind(binding);

        ViewGroup vg = (ViewGroup) binding.getRoot();
        builder.setView(vg);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    public void onBind(MemoPageBinding binding) {
        vbinding = binding;
        binding.header.setText(memo.id <= 0 ? R.string.page_overhead_new_agenda : R.string.page_overhead_edit_agenda);
        binding.img.setImageResource(R.drawable.blur_v1);
        binding.confirmBtn.setOnClickListener(
                (v)->onConfirm()
        );
        binding.cancelButton.setOnClickListener(
                (v)->onCancel()
        );

        associateTitle(binding.pageTitle);
        binding.pageTitle.setOnKeyListener((v, keyCode, event)->keyCode == KeyEvent.KEYCODE_ENTER);
        binding.tagContentTv.setText(memo.contents);
        binding.tagTagsTv.setText(memo.getTagsString());
        binding.deleteButton.setVisibility(memo.id == -1 ? View.GONE : View.VISIBLE);
        binding.deleteButton.setOnClickListener((v)->onDelete());

        /**
        RecyclerView recyclerView = binding.pageSections;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);


        final MemoSectionAdapter adapter = new MemoSectionAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setScrollToFunc((oid, id)-> scrollTo(id, recyclerView));
        adapter.setBinaryLabel(
                new MemoSectionAdapter.Label(
                        binding.pageYellowLabel,
                        binding.pageYellowLabelText,
                        binding.pageYellowLabelIcon
                ),
                new MemoSectionAdapter.Label(
                        binding.pageYellowLabel2,
                        binding.pageYellowLabel2Text,
                        binding.pageYellowLabel2Icon
                )
        );
        int expandId = adapter.setMemo(Memo, activityLId);
        recyclerView.post(()-> scrollTo(expandId, recyclerView));
         */
    }

    public void scrollTo(int pos, RecyclerView recyclerView) {
        Context rContext = recyclerView.getContext();
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(rContext){
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        smoothScroller.setTargetPosition(pos > 0 ? pos-1 : 0);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null)
            layoutManager.startSmoothScroll(smoothScroller);
    }

    public void associateTitle(EditText editText) {
        editText.setText(memo.title);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                memo.title = new SpannableStringBuilder(editText.getText().toString());
            }
        };
        editText.setTag(textWatcher);
        editText.addTextChangedListener(textWatcher);
    }

    public void onConfirm() {
        if (memo.title.toString().isEmpty()) {
            Toast.makeText(getContext(), R.string.page_no_title_warning, Toast.LENGTH_SHORT).show();
            return;
        }
        if (saveMemo())
            dialog.dismiss();
    }
    public void onCancel() {
        dialog.dismiss();
    }
    public void onDelete() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.memo_delete)
                .setMessage(R.string.memo_delete_confirm)
                .setIcon(R.drawable.ic_symb_cross)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    deleteMemo();
                    this.dialog.dismiss();
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    public boolean saveMemo() {
        TextInputEditText tiet = vbinding.tagContentTv;
        TagInputEditText tagtiet = vbinding.tagTagsTv;
        SpannableStringBuilder ssb = new SpannableStringBuilder(tiet.getText());
        ssb.clearSpans();
        if (ssb.length() == 0) {
            Toast.makeText(getContext(), R.string.tab_no_content_warning, Toast.LENGTH_SHORT).show();
            return false;
        }
        memo.contents = ssb;
        memo.tags = Arrays.asList(Objects.requireNonNull(tagtiet.getText()).toString().split(TagInputEditText.SEP));
        AppModule.retrieveMemoUseCases()
                .putMemoUseCase.invoke(memo);
        return true;
    }

    public boolean deleteMemo() {
        AppModule.retrieveMemoUseCases()
                .deleteMemoUseCase.invoke(memo);
        return true;
    }
}


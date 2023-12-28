package com.aurora.oasisplanner.data.core.use_cases.memo_usecases;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;

import com.aurora.oasisplanner.data.repository.MemoRepository;
import com.aurora.oasisplanner.presentation.dialog.memoeditdialog.MemoEditDialog;

public class EditMemoUseCase {
    private MemoRepository repository;
    private FragmentManager fragmentManager;

    public EditMemoUseCase(MemoRepository repository) {
        this.repository = repository;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    /** -1 indicates a new memo. */
    public void invoke(long memoId) {
        if (fragmentManager == null)
            throw new Resources.NotFoundException("Fragment Manager is Not Set Properly.");
        MemoEditDialog dialog = new MemoEditDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(MemoEditDialog.EXTRA_MEMO_ID, memoId);
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, "myDialog");
    }
}

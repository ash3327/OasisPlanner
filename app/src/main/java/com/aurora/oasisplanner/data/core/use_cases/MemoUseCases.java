package com.aurora.oasisplanner.data.core.use_cases;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.model.entities.memos._Memo;
import com.aurora.oasisplanner.data.repository.MemoRepository;
import com.aurora.oasisplanner.presentation.dialogs.memoeditdialog.MemoEditDialog;

public class MemoUseCases {
    private MemoRepository repository;
    private FragmentManager fragmentManager;

    public MemoUseCases(MemoRepository repository) {
        this.repository = repository;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    /** -1 indicates a new memo. */
    public void edit(long memoId) {
        if (fragmentManager == null)
            throw new Resources.NotFoundException("Fragment Manager is Not Set Properly.");

        Bundle bundle = new Bundle();
        bundle.putLong(MemoEditDialog.EXTRA_MEMO_ID, memoId);

        Navigation.findNavController(MainActivity.main, R.id.nav_host_fragment).navigate(
                R.id.navigation_memoEditDialog, bundle);
    }

    public _Memo get(long memoId) {
        return repository.getMemoFromId(memoId);
    }

    public void put(_Memo memo) {
        repository.insert(memo);
    }

    public void delete(_Memo memo) {
        repository.delete(memo);
    }
}

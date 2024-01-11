package com.aurora.oasisplanner.presentation.ui.memos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.datasource.AppDatabase;
import com.aurora.oasisplanner.data.model.entities.memos._Memo;
import com.aurora.oasisplanner.data.repository.MemoRepository;

import java.util.List;

public class MemoViewModel extends AndroidViewModel {
    private MemoRepository repository;
    public LiveData<List<_Memo>> memos;

    public MemoViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppModule.provideAppDatabase(application);
        repository = AppModule.provideMemoRepository(database);
        memos = repository.getMemos();
    }

    public void refreshMemos(String searchEntry) {
        memos = repository.requestMemos(searchEntry);
    }

    public void insert(_Memo memo) {
        repository.insert(memo);
    }

    public void update(_Memo memo) {
        repository.update(memo);
    }

    public void delete(_Memo memo) {
        repository.delete(memo);
    }

    public void deleteAllMemos() {
        repository.deleteAllMemos();
    }

    public LiveData<List<_Memo>> getMemos() {
        return memos;
    }
}

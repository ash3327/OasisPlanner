package com.aurora.oasisplanner.data.core.use_cases.memo_usecases;

import com.aurora.oasisplanner.data.model.entities._Memo;
import com.aurora.oasisplanner.data.repository.MemoRepository;

public class DeleteMemoUseCase {
    private MemoRepository repository;

    public DeleteMemoUseCase(MemoRepository repository) {
        this.repository = repository;
    }

    public void invoke(_Memo memo) {
        repository.delete(memo);
    }
}

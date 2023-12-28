package com.aurora.oasisplanner.data.core.use_cases.memo_usecases;

import com.aurora.oasisplanner.data.model.entities._Memo;
import com.aurora.oasisplanner.data.model.pojo.Agenda;
import com.aurora.oasisplanner.data.repository.AgendaRepository;
import com.aurora.oasisplanner.data.repository.MemoRepository;

public class GetMemoUseCase {
    private MemoRepository repository;

    public GetMemoUseCase(MemoRepository repository) {
        this.repository = repository;
    }

    public _Memo invoke(long memoId) {
        return repository.getMemoFromId(memoId);
    }
}

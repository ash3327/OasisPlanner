package com.aurora.oasisplanner.data.core.use_cases.memo_usecases;

public class MemoUseCases {
    public GetMemoUseCase getMemoUseCase;
    public EditMemoUseCase editMemoUseCase;
    public PutMemoUseCase putMemoUseCase;

    public MemoUseCases(
            GetMemoUseCase getMemoUseCase,
            EditMemoUseCase editMemoUseCase,
            PutMemoUseCase putMemoUseCase
    ) {
        this.getMemoUseCase = getMemoUseCase;
        this.editMemoUseCase = editMemoUseCase;
        this.putMemoUseCase = putMemoUseCase;
    }
}

package com.aurora.oasisplanner.data.core.use_cases.memo_usecases;

public class MemoUseCases {
    public GetMemoUseCase getMemoUseCase;
    public EditMemoUseCase editMemoUseCase;
    public PutMemoUseCase putMemoUseCase;
    public DeleteMemoUseCase deleteMemoUseCase;

    public MemoUseCases(
            GetMemoUseCase getMemoUseCase,
            EditMemoUseCase editMemoUseCase,
            PutMemoUseCase putMemoUseCase,
            DeleteMemoUseCase deleteMemoUseCase
    ) {
        this.getMemoUseCase = getMemoUseCase;
        this.editMemoUseCase = editMemoUseCase;
        this.putMemoUseCase = putMemoUseCase;
        this.deleteMemoUseCase = deleteMemoUseCase;
    }
}

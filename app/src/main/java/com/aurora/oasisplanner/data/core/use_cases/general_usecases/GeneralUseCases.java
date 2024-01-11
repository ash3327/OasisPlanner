package com.aurora.oasisplanner.data.core.use_cases.general_usecases;

import com.aurora.oasisplanner.data.core.use_cases.memo_usecases.DeleteMemoUseCase;
import com.aurora.oasisplanner.data.core.use_cases.memo_usecases.EditMemoUseCase;
import com.aurora.oasisplanner.data.core.use_cases.memo_usecases.GetMemoUseCase;
import com.aurora.oasisplanner.data.core.use_cases.memo_usecases.PutMemoUseCase;

public class GeneralUseCases {
    public GetTagUseCase getTagUseCase;

    public GeneralUseCases(
            GetTagUseCase getTagUseCase
    ) {
        this.getTagUseCase = getTagUseCase;
    }
}

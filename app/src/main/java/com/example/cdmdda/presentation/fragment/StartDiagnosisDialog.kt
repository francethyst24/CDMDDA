package com.example.cdmdda.presentation.fragment

import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.common.Constants
import com.example.cdmdda.common.utils.AndroidUtils.getStringArray
import com.example.cdmdda.domain.usecase.GetDiagnosisHistoryUseCase
import com.example.cdmdda.domain.usecase.GetDiseaseDiagnosisUseCase
import com.example.cdmdda.domain.usecase.GetPytorchMLUseCase
import com.example.cdmdda.domain.usecase.PrepareBitmapUseCase
import com.example.cdmdda.presentation.viewmodel.MainViewModel
import com.example.cdmdda.presentation.viewmodel.factory.activityViewModelBuilder

class StartDiagnosisDialog: AppCompatDialogFragment() {
    private val viewModel by activityViewModelBuilder {
        MainViewModel(
            intent.getBooleanExtra(Constants.INIT_QUERIES, false),
            GetDiagnosisHistoryUseCase(),
            GetDiseaseDiagnosisUseCase(
                PrepareBitmapUseCase(),
                GetPytorchMLUseCase(),
                getStringArray(Constants.LABELS),
            ),
        )
    }


}
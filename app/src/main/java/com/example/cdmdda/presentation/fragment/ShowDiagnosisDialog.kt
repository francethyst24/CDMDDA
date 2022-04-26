package com.example.cdmdda.presentation.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import com.example.cdmdda.common.utils.AndroidUtils.SHOW_DIAGNOSIS_REQUEST
import com.example.cdmdda.common.utils.AndroidUtils.SHOW_DIAGNOSIS_RESULT
import com.example.cdmdda.common.utils.AndroidUtils.getStringArray
import com.example.cdmdda.common.Constants.INIT_QUERIES
import com.example.cdmdda.common.Constants.LABELS
import com.example.cdmdda.domain.usecase.GetDiagnosisHistoryUseCase
import com.example.cdmdda.domain.usecase.GetDiseaseDiagnosisUseCase
import com.example.cdmdda.domain.usecase.GetPytorchMLUseCase
import com.example.cdmdda.domain.usecase.PrepareBitmapUseCase
import com.example.cdmdda.presentation.viewmodel.MainViewModel
import com.example.cdmdda.presentation.viewmodel.factory.activityViewModelBuilder

class ShowDiagnosisDialog : AppCompatDialogFragment() {
    companion object {
        const val TAG = "ShowDiagnosisDialog"
        const val HAS_LEAF = "has_leaf"

        fun newInstance(hasLeaf: Boolean): ShowDiagnosisDialog {
            val returnFragment = ShowDiagnosisDialog()
            returnFragment.arguments = bundleOf(HAS_LEAF to hasLeaf)
            return returnFragment
        }

    }

    private val viewModel by activityViewModelBuilder {
        MainViewModel(
            intent.getBooleanExtra(INIT_QUERIES, false),
            GetDiagnosisHistoryUseCase(),
            GetDiseaseDiagnosisUseCase(
                PrepareBitmapUseCase(),
                GetPytorchMLUseCase(),
                getStringArray(LABELS),
            ),
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = with(viewModel) {
        val hasLeaf = savedInstanceState?.getBoolean(HAS_LEAF) ?: false
        isCancelable = false
        val case = getString(if (hasLeaf) uiTextDisease else uiTextLeaf)
        val builder = AlertDialog.Builder(requireActivity())
            .setTitle(getString(uiDescDiagnosisFail, case))
            .setNegativeButton(uiTextOk, null)
            .setPositiveButton(uiTextLearnMore) { _, _ ->
                parentFragmentManager.setFragmentResult(
                    SHOW_DIAGNOSIS_REQUEST,
                    bundleOf(SHOW_DIAGNOSIS_RESULT to true)
                )
            }
        return builder.create()
    }


}
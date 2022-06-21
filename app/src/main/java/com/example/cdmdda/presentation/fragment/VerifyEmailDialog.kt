package com.example.cdmdda.presentation.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.cdmdda.common.Constants.INIT_QUERIES
import com.example.cdmdda.common.Constants.LABELS
import com.example.cdmdda.common.utils.AndroidUtils.getStringArray
import com.example.cdmdda.domain.usecase.*
import com.example.cdmdda.presentation.viewmodel.MainViewModel
import com.example.cdmdda.presentation.viewmodel.factory.activityViewModelBuilder

class VerifyEmailDialog : AppCompatDialogFragment() {
    companion object {
        const val TAG = "VerifyEmailDialog"
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
            AddDiseaseDiagnosisUseCase(prepareBitmapUseCase = PrepareBitmapUseCase()),
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = with(viewModel.currentUser!!) {
        isCancelable = false
        reload().addOnCompleteListener {
            if (it.isSuccessful && isEmailVerified) dismiss()
        }
        return with(viewModel) {
            val builder = AlertDialog.Builder(requireActivity())
                .setMessage(getString(uiDescVerifyEmail).plus(" $email"))
                .setTitle(uiHeadVerifyEmail)
                .setPositiveButton(uiTextOk, null)
            builder.create()
        }
    }
}
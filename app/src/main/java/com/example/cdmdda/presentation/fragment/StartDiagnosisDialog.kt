package com.example.cdmdda.presentation.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatDialogFragment
import com.bumptech.glide.Glide
import com.example.cdmdda.common.Constants
import com.example.cdmdda.common.utils.AndroidUtils.START_DIAGNOSIS_REQUEST
import com.example.cdmdda.common.utils.AndroidUtils.getStringArray
import com.example.cdmdda.databinding.FragmentStartDiagnosisBinding
import com.example.cdmdda.domain.model.Diagnosable
import com.example.cdmdda.domain.usecase.GetDiagnosisHistoryUseCase
import com.example.cdmdda.domain.usecase.GetDiseaseDiagnosisUseCase
import com.example.cdmdda.domain.usecase.GetPytorchMLUseCase
import com.example.cdmdda.domain.usecase.PrepareBitmapUseCase
import com.example.cdmdda.presentation.viewmodel.MainViewModel
import com.example.cdmdda.presentation.viewmodel.factory.activityViewModelBuilder

class StartDiagnosisDialog : AppCompatDialogFragment() {
    companion object {
        const val TAG = "StartDiagnosisDialog"
    }

    interface OnDiagnosisResultListener {
        fun onGotoDiseaseClick(diseaseId: String)
    }

    private var listener: OnDiagnosisResultListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnDiagnosisResultListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private val model by activityViewModelBuilder {
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
    private var _binding: FragmentStartDiagnosisBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        _binding = FragmentStartDiagnosisBinding.inflate(
            LayoutInflater.from(requireActivity())
        )
        setImageView()
        setDialogButtons()
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }

    private fun setImageView() {
        model.userDiagnosableState.observe(this) { diagnosable ->
            if (diagnosable == null) return@observe
            binding.root.context.let {
                Glide.with(it).load(
                    when (diagnosable) {
                        is Diagnosable.Bmp -> diagnosable.value
                        is Diagnosable.Uri -> diagnosable.value
                    }
                ).into(binding.imageDiagnosable)
            }
        }
    }

    private fun setDialogButtons() {
        binding.buttonCancelDiagnosis.setOnClickListener {
            with(model) {
                cancelDiagnosis()
                finishDiagnosableSubmission()
                clearDiagnosisResult()
            }
            parentFragmentManager.clearFragmentResult(START_DIAGNOSIS_REQUEST)
            dismiss()
        }
        model.diagnosisResultState.observe(this) { result ->
            if (result == null) return@observe
            binding.textResult.text = result
            binding.buttonGotoDisease.apply {
                text = getString(model.uiTextLearnMore)
                isClickable = true
                isEnabled = true
                setOnClickListener {
                    /*parentFragmentManager.setFragmentResult(
                        START_DIAGNOSIS_REQUEST,
                        bundleOf(START_DIAGNOSIS_RESULT to true)
                    )*/
                    listener?.onGotoDiseaseClick(result)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
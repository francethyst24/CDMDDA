package com.example.cdmdda.presentation.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.bumptech.glide.Glide
import com.example.cdmdda.data.dto.DiseaseDiagnosis
import com.example.cdmdda.data.repository.ImageRepository
import com.example.cdmdda.databinding.FragmentStartDiagnosisBinding
import com.example.cdmdda.databinding.FragmentStartDiagnosisBinding.inflate
import com.example.cdmdda.presentation.viewmodel.ShowDiagnosisDialogViewModel
import com.example.cdmdda.presentation.viewmodel.factory.activityViewModelBuilder

class ShowDiagnosisDialog : AppCompatDialogFragment() {
    companion object {
        const val TAG = "ShowDiagnosisDialog"
    }

    interface OnGotoDiseaseListener {
        fun onGotoDiseaseClick(diseaseId: DiseaseDiagnosis)
    }

    private var listener: OnGotoDiseaseListener? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as OnGotoDiseaseListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    private val model by activityViewModelBuilder {
        ShowDiagnosisDialogViewModel(ImageRepository())
    }

    private var _binding: FragmentStartDiagnosisBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        isCancelable = false
        _binding = inflate(LayoutInflater.from(requireActivity()))
        setImageView()
        setDialogButtons()
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }

    private fun setDialogButtons() {
        model.currentDiagnosisUiState.observe(this) { diagnosis ->
            if (diagnosis == null) return@observe
            binding.textResult.text = buildString {
                append("The image is ")
                val toPercent = diagnosis.confidenceLvl * 100
                append(String.format("%3d", toPercent.toInt()))
                append("% ")
                append(diagnosis.id)
            }
            binding.buttonGotoDisease.apply {
                text = getString(model.uiTextLearnMore)
                isClickable = true
                isEnabled = true
                setOnClickListener { listener?.onGotoDiseaseClick(diagnosis) }
            }
        }
        binding.buttonCancelDiagnosis.setOnClickListener {
            model.clearCachedDiagnosis()
            dismiss()
        }
    }

    private fun setImageView() {
        model.diagnosisImageUiState.observe(this) { uri ->
            if (uri == null) return@observe
            Glide.with(this)
                .load(uri)
                .into(binding.imageDiagnosable)
            binding.loadingImage.hide()
            binding.imageDiagnosable.imageTintList = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
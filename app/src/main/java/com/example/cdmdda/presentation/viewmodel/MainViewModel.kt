package com.example.cdmdda.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.provider.SearchRecentSuggestions
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.cdmdda.data.dto.CropItem
import com.example.cdmdda.data.dto.UserInput
import com.example.cdmdda.data.repository.DiagnosisRepository
import com.example.cdmdda.data.repository.SearchQueryRepository
import com.example.cdmdda.domain.usecase.*
import com.example.cdmdda.presentation.helper.ResourceHelper
import kotlinx.coroutines.*
import org.pytorch.IValue
import org.pytorch.Module
import org.pytorch.torchvision.TensorImageUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MainViewModel(
    private val diagnoseDiseaseUseCase: DiagnoseDiseaseUseCase,
    private val getDiagnosisHistoryUseCase: GetDiagnosisHistoryUseCase,
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val getCropItemUseCase: GetCropItemUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ViewModel() {
    companion object { private const val TAG = "MainViewModel" }

    // declare: Firebase(Auth)
    val currentUser get() = getAuthStateUseCase()
    fun signOut(provider: SearchRecentSuggestions) = getAuthStateUseCase.signOut(provider)

    private val _cropList = mutableListOf<CropItem>()
    val cropList : List<CropItem> = _cropList

    fun cropCount(helper: ResourceHelper) = liveData {
        for (id in helper.allCrops) {
            val newCrop = getCropItemUseCase(id)
            _cropList.add(newCrop)
            _cropList.sortBy { it.name }
            emit(_cropList.indexOf(newCrop))
        }
    }

    suspend fun isUserAuthenticated(provider: SearchRecentSuggestions) = withContext(ioDispatcher) {
        if (currentUser == null) return@withContext false
        currentUser?.apply {
            getDiagnosisHistoryUseCase(DiagnosisRepository(uid))
            val repository = SearchQueryRepository(uid)
            repository.deleteCache(provider)
            repository.fetchOnline(provider)
        }
        return@withContext true
    }

    override fun onCleared() {
        super.onCleared()
        getDiagnosisHistoryUseCase.onViewModelCleared()
    }

    suspend fun getFirestoreRecyclerOptions() = withContext(ioDispatcher) {
        getDiagnosisHistoryUseCase.recyclerOptions
    }

    fun saveDiagnosis(diseaseId: String) {
        if (diseaseId == "Healthy") return
        viewModelScope.launch(ioDispatcher) {
            getDiagnosisHistoryUseCase.add(diseaseId)
                ?.addOnSuccessListener { Log.d(TAG, "Firebase write success") }
                ?.addOnFailureListener { Log.w(TAG, "Firebase write failure", it) }
        }
    }
    // endregion

    fun startDiagnosisAsync(context: Context, input: UserInput): Deferred<String> {
        return viewModelScope.async { diagnoseDiseaseUseCase(context, input) }
    }

    fun cancelDiagnosisAsync() = diagnoseDiseaseUseCase.cancelDiagnosis()

    suspend fun ran(context: Context, userInput: UserInput) = context.run {
        val prepareBitmapUseCase = PrepareBitmapUseCase()
        prepareBitmapUseCase(userInput, contentResolver)?.let { ran(this, it) }
    }

    private fun ran(context: Context, bitmap: Bitmap) = context.run {
        val module = Module.load(assetFilePath(context, "model.ptl"))
        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
            bitmap,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
            TensorImageUtils.TORCHVISION_NORM_STD_RGB
        )
        val outputTensor = module.forward(IValue.from(inputTensor)).toTensor()
        val scores = outputTensor.dataAsFloatArray

        val maxScore = scores.maxOrNull() ?: -Float.MAX_VALUE
        val maxScoreIndex = scores.indexOfFirst { it == maxScore }

        val getClassesOutput = module.runMethod("get_classes")
        val classesListIValue = getClassesOutput.toList()
        val moduleClasses = mutableListOf<String>().let { temp ->
            for (iv in classesListIValue) temp.add(iv.toStr())
            temp.toList()
        }
        moduleClasses[maxScoreIndex]
    }

    private fun assetFilePath(context: Context, asset: String): String {
        val file = File(context.filesDir, asset)
        try {
            val inpStream: InputStream = context.assets.open(asset)
            try {
                val outStream = FileOutputStream(file, false)
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (true) {
                    read = inpStream.read(buffer)
                    if (read == -1) break
                    outStream.write(buffer, 0, read)
                }
                outStream.flush()
            } catch (e: Exception) { e.printStackTrace() }
            return file.absolutePath
        } catch (e: Exception) { e.printStackTrace() }
        return String()
    }

}
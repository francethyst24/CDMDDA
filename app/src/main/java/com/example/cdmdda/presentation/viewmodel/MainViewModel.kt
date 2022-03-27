package com.example.cdmdda.presentation.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.cdmdda.data.dto.CropItemUiState
import com.example.cdmdda.data.dto.DiseaseDiagnosisUiState
import com.example.cdmdda.data.dto.UserInput
import com.example.cdmdda.data.repository.DiagnosisRepository
import com.example.cdmdda.ml.DiseaseDetection
import com.example.cdmdda.presentation.helper.GlobalHelper
import com.example.cdmdda.presentation.helper.ResourceHelper
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.ChangeEventListener
import com.firebase.ui.firestore.ClassSnapshotParser
import com.firebase.ui.firestore.FirestoreArray
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class MainViewModel(
    private val defaultDispatcher: CoroutineDispatcher,
    private val ioDispatcher: CoroutineDispatcher,
) : ViewModel() {
    companion object { private const val TAG = "MainViewModel" }

    // declare: Firebase(Auth)
    private val auth: FirebaseAuth = Firebase.auth
    val currentUser = auth.currentUser
    fun signOut() = auth.signOut()

    private val _cropList = mutableListOf<CropItemUiState>()
    val cropList : List<CropItemUiState> = _cropList

    fun cropCount(resourceHelper: ResourceHelper, cropIds: Array<String>) = liveData {
        for (id in cropIds) {
            val cropItemUiState = viewModelScope.async(defaultDispatcher) {
                CropItemUiState(
                    id,
                    resourceHelper.fetchCropName(id),
                    resourceHelper.fetchCropIsSupported(id),
                    resourceHelper.fetchCropBannerId(id)
                )
            }
            _cropList.add(cropItemUiState.await())
            emit(_cropList.size - 1)
        }
    }

    // region // declare: Firestore(Repository, RecyclerOptions)
    private val diagnosisRepository = DiagnosisRepository(Firebase.firestore, Firebase.auth.uid.toString())
    var diagnosisHistory: FirestoreArray<DiseaseDiagnosisUiState>? = null

    suspend fun isUserAuthenticated() : Boolean = withContext(ioDispatcher) {
        if (currentUser == null) return@withContext false
        val query = diagnosisRepository.getDiagnosisRecyclerOptions()
        diagnosisHistory = FirestoreArray(query, ClassSnapshotParser(DiseaseDiagnosisUiState::class.java)).also {
            it.addChangeEventListener(KeepAliveListener)
        }
        return@withContext true
    }

    override fun onCleared() {
        super.onCleared()
        diagnosisHistory?.removeChangeEventListener(KeepAliveListener)
    }

    suspend fun getFirestoreRecyclerOptions() = withContext(ioDispatcher) {
        diagnosisHistory?.let { firestoreArray ->
            FirestoreRecyclerOptions.Builder<DiseaseDiagnosisUiState>()
                .setSnapshotArray(firestoreArray)
                .build()
        }
    }

    fun saveDiagnosis(diseaseId: String) {
        if (diseaseId == "Healthy") return
        viewModelScope.launch(ioDispatcher) {
            diagnosisRepository.saveDiagnosis(diseaseId)
                .addOnSuccessListener { Log.d(TAG, "Firebase write success") }
                .addOnFailureListener { Log.w(TAG, "Firebase write failure", it) }
        }
    }
    // endregion

    // region // ml: Bitmap -> Dispatchers.IO -> inference: String
    private val dim: Int = GlobalHelper.DIM
    private var inferenceJob = Job()
    private var inferenceScope = CoroutineScope(defaultDispatcher)

    fun runInference(
        context: Context,
        _labels: Array<String>,
        input: UserInput
    ) = liveData(inferenceJob + defaultDispatcher) {
        inferenceScope.apply {
            val bitmap = convertBitmap(input, context.contentResolver)
            val bitmapRescaled  = rescaleBitmap(bitmap, true)
            val labels = _labels.toList()
            val defaultIndex = indexOfFirst(labels, "null")

            runCatching { DiseaseDetection.newInstance(context) }.fold(
                onSuccess = { model ->
                    tflite(model, bitmapRescaled).also { inferences ->
                        val index = indexOfFirst(inferences.toList(), 1.0f)
                        emit(labels[if (index != -1) index else defaultIndex])
                    }
                },
                onFailure = {
                    Log.e(TAG, "runInference: Error retrieving model", it)
                    emit(labels[defaultIndex])
                }
            )
        }
    }

    // region // suspend fun: runInference

    private suspend fun convertBitmap(input: UserInput, contentResolver: ContentResolver) = withContext(defaultDispatcher) {
        input.toBitmap(contentResolver)
    }

    private suspend fun rescaleBitmap(bitmap: Bitmap, filter: Boolean) = withContext(defaultDispatcher) {
        Bitmap.createScaledBitmap(bitmap, dim, dim, filter)
    }

    private suspend fun <T> indexOfFirst(list: List<T>, predicate: T) = withContext(defaultDispatcher) {
        list.indexOfFirst { it == predicate }
    }
    // endregion

    fun cancelInference() {
        inferenceJob.cancel()
        inferenceJob = Job()
    }

    private suspend fun tflite(model: DiseaseDetection, bitmap: Bitmap): FloatArray {
        return withContext(defaultDispatcher) {
            val dataType = DataType.FLOAT32
            val tensorImage = getTensorImage(bitmap, dataType)

            // Creates input for reference.
            val inputFeature0 = getTensorBuffer(dataType)
            inputFeature0.loadBuffer(tensorImage.buffer)

            // Runs model inference and gets result.
            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            outputFeature0.floatArray
        }

    }

    // region // suspend fun: tflite

    private suspend fun getTensorImage(bitmap: Bitmap, type: DataType): TensorImage = withContext(defaultDispatcher) {
        TensorImage.createFrom(TensorImage.fromBitmap(bitmap), type)
    }

    private suspend fun getTensorBuffer(type: DataType): TensorBuffer = withContext(defaultDispatcher) {
        TensorBuffer.createFixedSize(intArrayOf(1, dim, dim, 3), type)
    }
    // endregion

    @Suppress("DEPRECATION")
    private fun UserInput.toBitmap(resolver: ContentResolver) = when (this) {
        is UserInput.Uri -> {
            if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(resolver, this.value)
            } else {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, this.value))
                    .copy(Bitmap.Config.ARGB_8888, true)
            }
        }
        is UserInput.Bitmap -> this.value
    }
    // endregion

    private object KeepAliveListener : ChangeEventListener {
        override fun onChildChanged(type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) =
            Unit
        override fun onDataChanged() = Unit
        override fun onError(e: FirebaseFirestoreException) = Unit
    }

}
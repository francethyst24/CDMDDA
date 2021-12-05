package com.example.cdmdda.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.example.cdmdda.R
import com.example.cdmdda.ml.DiseaseDetectorV3
import com.example.cdmdda.model.FirestoreRepository
import com.example.cdmdda.model.dto.Diagnosis
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

class MainViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "MainViewModel"
    }
    private val context: Context get() = getApplication<Application>().applicationContext

    // declare: Firebase(Auth)
    private var auth: FirebaseAuth = Firebase.auth

    // region // declare: Firestore(Repository, RecyclerOptions)
    private var repository = FirestoreRepository(application.getString(R.string.dataset))
    val mainRecyclerOptions = repository.getCropRecyclerOptions()
    lateinit var diagnosisRecyclerOptions : FirestoreRecyclerOptions<Diagnosis>

    fun getUserDiagnosisHistory() : Boolean {
        if (auth.currentUser == null) return false
        diagnosisRecyclerOptions = repository.getDiagnosisRecyclerOptions()
        return true
    }

    fun saveDiagnosis(diseaseId: String) {
        if (diseaseId == "Healthy") return
        repository.saveDiagnosis(diseaseId)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }
    // endregion

    // region // ml: Bitmap -> Dispatchers.IO -> inference: String
    private val dim: Int = context.getString(R.string.dim).toInt()
    private var inferenceJob = Job()

    @Suppress("BlockingMethodInNonBlockingContext", "DEPRECATION")
    fun runInference(input: Any)
        = liveData(inferenceJob + Dispatchers.IO) {
        val rescaledBitmap = Bitmap.createScaledBitmap(anyToBitmap(input), dim, dim, true)
        val labels = mutableListOf<String>()
        context.assets.open(context.getString(R.string.labels)).bufferedReader().forEachLine {
            labels.add(it.trim())
        }
        val defaultIndex = labels.indexOfFirst { it == "null" }
        tflite(rescaledBitmap).apply {
            val index = indexOfFirst { it == 1.0f }
            emit(labels[if (index != -1) index else defaultIndex])
        }
    }

    fun cancelInference() { inferenceJob.cancel(); inferenceJob = Job() }

    private fun tflite(bitmap: Bitmap): FloatArray {
        val model = DiseaseDetectorV3.newInstance(context)
        val tensorImage = TensorImage.createFrom(TensorImage.fromBitmap(bitmap), DataType.FLOAT32)

        // Creates input for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, dim, dim, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(tensorImage.buffer)
        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        return outputFeature0.floatArray
    }

    @Suppress("DEPRECATION")
    private fun anyToBitmap(any: Any) : Bitmap = if (any is Uri) {
        if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(context.contentResolver, any)
        } else {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, any))
                .copy(Bitmap.Config.ARGB_8888, true)
        }
    } else any as Bitmap
    // endregion

}
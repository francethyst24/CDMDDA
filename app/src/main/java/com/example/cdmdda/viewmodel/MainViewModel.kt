package com.example.cdmdda.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.example.cdmdda.model.FirestoreRepository
import com.example.cdmdda.model.dto.Diagnosis
import com.example.cdmdda.R
import com.example.cdmdda.ml.DiseaseDetectorV3
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
    companion object { private const val TAG = "MainViewModel" }

    // declare: Firebase(Auth)
    private var auth: FirebaseAuth = Firebase.auth

    // region // declare: Firestore(Repository, RecyclerOptions)
    private var repository = FirestoreRepository(application.getString(R.string.dataset))
    val mainRecyclerOptions = repository.getCropRecyclerOptions()
    lateinit var diagnosisRecyclerOptions : FirestoreRecyclerOptions<Diagnosis>
    // endregion

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

    // region // ml: Bitmap -> Dispatchers.IO -> inference: String
    private var inferenceJob = Job()
    fun runInference(bitmap: Bitmap) = liveData(inferenceJob + Dispatchers.IO) {
        val dim = 224; var defaultIndex = -1; val inference: String
        val rescaled = Bitmap.createScaledBitmap(bitmap, dim, dim, true)
        val context = getApplication<Application>().applicationContext
        val labels = mutableListOf<String>().apply {
            context.assets.open(context.getString(R.string.labels)).bufferedReader()
                .forEachLine { this.add(it.trim()) }
            defaultIndex = this.indexOfFirst { it == "null" }
        }
        val model = DiseaseDetectorV3.newInstance(context)
        val tensorImage = TensorImage.createFrom(TensorImage.fromBitmap(rescaled), DataType.FLOAT32)

        // Creates input for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, dim, dim, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(tensorImage.buffer)
        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        outputFeature0.floatArray.apply {
            val index = indexOfFirst { it == 1.0f }
            inference = labels[if (index != -1) { index } else defaultIndex]
        }
        emit(inference)
    }

    fun cancelInference() { inferenceJob.cancel(); inferenceJob = Job() }
    // endregion

    /*
    private var supportedDiseases = MutableLiveData<List<String>>()

    fun getSupportedDiseases() : LiveData<List<String>> {
        repository.getSupportedDiseases().addSnapshotListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen to disease documents failed.", e)
                supportedDiseases.value = null
                return@addSnapshotListener
            }

            var mutableSupportedDiseases = mutableListOf<String>()
            for (document in value!!) {
                val disease = document.toObject(Disease::class.java)
                mutableSupportedDiseases.add(disease.name!!)
            }

            supportedDiseases.value = mutableSupportedDiseases
        }
        return supportedDiseases
    }
     */

}
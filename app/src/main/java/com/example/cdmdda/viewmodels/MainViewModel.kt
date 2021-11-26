package com.example.cdmdda.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.cdmdda.R
import com.example.cdmdda.dto.Crop
import com.example.cdmdda.dto.Diagnosis
import com.example.cdmdda.ml.DiseaseDetectorV3
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.sql.Timestamp

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object { private const val TAG = "MainViewModel" }

    // region -- declare: Firebase - Auth, Firestore
    private var auth: FirebaseAuth = Firebase.auth
    private var db: FirebaseFirestore = Firebase.firestore
    // endregion

    // region -- init: Collection
    private val cropRef = db.collection("crop_sets")
        .document(application.getString(R.string.dataset))
        .collection("crops")
    private val diseaseRef = db.collection("disease_sets")
        .document(application.getString(R.string.dataset))
        .collection("diseases")
    private val countRef = db.collection("counters").document("diseases")

    // endregion

    var user: FirebaseUser? = if (auth.currentUser != null) { auth.currentUser } else { null }
    var mainRecyclerOptions = FirestoreRecyclerOptions.Builder<Crop>()
        .setQuery(cropRef.orderBy("name"), Crop::class.java)
        .build()


    private val diagnosisRef = db.collection("diagnosis")
    private lateinit var diagnosisQuery: Query
    lateinit var diagnosisRecyclerOptions : FirestoreRecyclerOptions<Diagnosis>


    var diseaseList = ArrayList<String>().apply {
        diseaseRef.get().addOnCompleteListener {
            if (it.isSuccessful) for (document in it.result!!) this.add(document.id)
        }
    }


    fun reload() : Boolean {
        return (auth.currentUser != null).also {
            if (auth.currentUser != null) {
                user = auth.currentUser!!
                diagnosisQuery = diagnosisRef.whereEqualTo("user_id", user!!.uid)
                    .orderBy("diagnosed_on", Query.Direction.DESCENDING)

                diagnosisRecyclerOptions = FirestoreRecyclerOptions.Builder<Diagnosis>()
                    .setQuery(diagnosisQuery, Diagnosis::class.java)
                    .build()
            }
        }
    }

    fun addDiagnosis(diseaseId: String) {
        if (diseaseId == "Healthy") return
        val diagnosis = hashMapOf(
            "name" to diseaseId,
            "user_id" to auth.uid,
            "diagnosed_on" to Timestamp(System.currentTimeMillis())
        )

        diagnosisRef.document(diseaseId).set(diagnosis)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error writing document", e)
            }
    }

    fun runInference(bitmap: Bitmap, dim: Int = 224) = GlobalScope.async {
        val value: String
        val context = getApplication<Application>().applicationContext
        val model = DiseaseDetectorV3.newInstance(context)
        val rescaled = Bitmap.createScaledBitmap(bitmap, dim, dim, true)
        val tensorImage = TensorImage.createFrom(TensorImage.fromBitmap(rescaled), DataType.FLOAT32)
        // Creates input for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, dim, dim, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(tensorImage.buffer)
        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val labels = mutableListOf<String>()
        context.assets.open(context.getString(R.string.labels)).bufferedReader()
            .forEachLine { labels.add(it.trim()) }

        val defaultIndex = labels.indexOfFirst { it == "null" }
        outputFeature0.floatArray.apply {
            val index = indexOfFirst { it == 1.0f }
            value = labels[if (index != -1) { index - 1 } else defaultIndex]
        }
        value
    }

}
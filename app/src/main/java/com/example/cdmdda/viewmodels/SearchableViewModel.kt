package com.example.cdmdda.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.cdmdda.R
import com.example.cdmdda.adapters.SearchAdapter
import com.example.cdmdda.dto.Disease
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchableViewModel(application: Application, query: String) : AndroidViewModel(application) {
    companion object { private const val TAG = "SearchableViewModel" }

    private var db: FirebaseFirestore = Firebase.firestore
    val diseaseRef = db.collection("disease_sets")
        .document(application.getString(R.string.dataset))
        .collection("diseases")

    var diseaseListOrig = ArrayList<String>().apply {
        diseaseRef.get().addOnCompleteListener {
            if (it.isSuccessful) for (document in it.result!!) this.add(document.id)
        }
    }

    val diseaseList = mutableListOf<String>().apply {
        diseaseRef.get().addOnCompleteListener {
            if (it.isSuccessful) {
                for (document in it.result!!) {
                    this.add(document.id)
                }
            }
        }
    } as List<String>

    /*
    var recyclerOptions : FirestoreRecyclerOptions<Disease>

    init {
        var limit: String
        query.apply { limit = dropLast(1).plus((this[length-1] + 1)) }
        recyclerOptions = FirestoreRecyclerOptions.Builder<Disease>()
            .setQuery(
                diseaseRef
                    .whereGreaterThanOrEqualTo("name", query)
                    .whereLessThan("name", limit),
                Disease::class.java
            ).build()
    }

     */
}
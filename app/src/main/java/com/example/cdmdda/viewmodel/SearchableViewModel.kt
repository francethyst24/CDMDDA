package com.example.cdmdda.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.cdmdda.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchableViewModel(application: Application, query: String) : AndroidViewModel(application) {
    val toolbarTitle = application.getString(R.string.title_search_toolbar).plus(query)
}
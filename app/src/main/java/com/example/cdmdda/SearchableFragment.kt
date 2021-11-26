package com.example.cdmdda

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.ListFragment
import androidx.lifecycle.ViewModelProvider
import com.example.cdmdda.databinding.FragmentSearchableBinding
import com.example.cdmdda.viewmodels.SearchableViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchableFragment : ListFragment() {

    private var _binding: FragmentSearchableBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel: SearchableViewModel

    private var db: FirebaseFirestore = Firebase.firestore
    private val diseaseRef = db.collection("diseases")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProvider(this)[SearchableViewModel::class.java]
        _binding = FragmentSearchableBinding.inflate(layoutInflater, container, false)

        // Verify the action and get the query
        val intent = activity!!.intent
        if (Intent.ACTION_SEARCH == intent.action) {
            intent.getStringExtra(SearchManager.QUERY)?.also { query ->
                viewModel.doMySearch(query)
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
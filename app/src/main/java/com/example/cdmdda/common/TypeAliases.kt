package com.example.cdmdda.common

import com.example.cdmdda.data.dto.DiseaseDiagnosis
import com.firebase.ui.firestore.FirestoreArray
import com.firebase.ui.firestore.FirestoreRecyclerOptions

typealias StringArray = Array<String>
typealias StringList = List<String>
typealias DiagnosisUiState = DiseaseDiagnosis
typealias FirestoreDiagnosisArray = FirestoreArray<DiagnosisUiState>
typealias FirestoreDiagnosisRecyclerOptions = FirestoreRecyclerOptions<DiagnosisUiState>
typealias FirestoreDiagnosisRecyclerOptionsBuilder = FirestoreRecyclerOptions.Builder<DiagnosisUiState>
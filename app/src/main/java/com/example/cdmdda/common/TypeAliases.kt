package com.example.cdmdda.common

import android.net.Uri
import android.os.Parcelable
import com.example.cdmdda.data.dto.DiseaseDiagnosis
import com.firebase.ui.firestore.FirestoreArray
import com.firebase.ui.firestore.FirestoreRecyclerOptions

typealias StringArray = Array<String>
typealias StringList = List<String>
typealias DiagnosisUiState = DiseaseDiagnosis
typealias FirestoreDiagnosisArray = FirestoreArray<DiagnosisUiState>
typealias DiagnosisRecyclerOptions = FirestoreRecyclerOptions<DiagnosisUiState>
typealias DiagnosisRecyclerOptionsBuilder = FirestoreRecyclerOptions.Builder<DiagnosisUiState>

typealias BoolCallback = (Boolean) -> Unit
typealias StringCallback = (String) -> Unit
typealias UriCallback = (Uri) -> Unit
typealias ParcelCallback = (Parcelable) -> Unit
typealias IntCallback = (Int) -> Unit
typealias Callback = () -> Unit

typealias Diagnosis = DiseaseDiagnosis
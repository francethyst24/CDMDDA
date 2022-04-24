package com.example.cdmdda.common

import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import com.example.cdmdda.data.dto.DiseaseDiagnosis
import com.example.cdmdda.domain.model.Image
import com.firebase.ui.firestore.FirestoreArray
import com.firebase.ui.firestore.FirestoreRecyclerOptions

typealias StringArray = Array<String>
typealias StringList = List<String>
typealias ImageList = List<Image>

typealias DiagnosisUiState = DiseaseDiagnosis
typealias FirestoreDiagnosisArray = FirestoreArray<DiagnosisUiState>
typealias DiagnosisRecyclerOptions = FirestoreRecyclerOptions<DiagnosisUiState>
typealias DiagnosisRecyclerOptionsBuilder = FirestoreRecyclerOptions.Builder<DiagnosisUiState>

typealias Callback = () -> Unit
typealias IntCallback = (Int) -> Unit
typealias BoolCallback = (Boolean) -> Unit
typealias StringCallback = (String) -> Unit
typealias UriCallback = (Uri) -> Unit
typealias ParcelCallback = (Parcelable) -> Unit
typealias ImageCallback = (Image) -> Unit
typealias BundleCallback = (Bundle) -> Unit

typealias Diagnosis = DiseaseDiagnosis
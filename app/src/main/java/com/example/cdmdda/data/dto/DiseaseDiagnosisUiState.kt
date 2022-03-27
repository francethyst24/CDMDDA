package com.example.cdmdda.data.dto

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class DiseaseDiagnosisUiState (
    @get:PropertyName("name")
    @set:PropertyName("name")
    override var id : String = String(),
    val user_id : String = String(),
    val diagnosed_on : Timestamp = Timestamp(Date()),
) : DiseaseUiState(id), Parcelable
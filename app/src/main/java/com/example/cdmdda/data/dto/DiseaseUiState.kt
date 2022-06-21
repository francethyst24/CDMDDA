package com.example.cdmdda.data.dto

import android.content.Context
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize
import java.util.*

sealed class DiseaseUiState(open val id: String)

@Parcelize
data class DiseaseDiagnosis constructor(
    @DocumentId
    val documentId: String = String(),
    @get:PropertyName("name") @set:PropertyName("name")
    override var id: String = String(),
    @get:PropertyName("diagnosed_on") @set:PropertyName("diagnosed_on")
    var diagnosedOn: Timestamp = Timestamp(Date()),
    @get:PropertyName("confidence_lvl") @set:PropertyName("confidence_lvl")
    var confidenceLvl: Float = Float.MIN_VALUE,
) : DiseaseUiState(id), Parcelable

@Parcelize
data class DiseaseItem constructor(
    override val id: String = String(),
    val isDetectable: Boolean = false,
) : DiseaseUiState(id), Parcelable

@Parcelize
data class DiseaseText constructor(
    override val id: String = String(),
) : DiseaseUiState(id), UiState, Parcelable {
    override fun displayName(context: Context) = id
}
package com.example.cdmdda.data.dto

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class DiseaseDiagnosis(
    @get:PropertyName("name") @set:PropertyName("name")
    override var id: String = String(),

    @get:PropertyName("diagnosed_on") @set:PropertyName("diagnosed_on")
    var diagnosedOn: Timestamp = Timestamp(Date()),
) : Disease(id), Parcelable
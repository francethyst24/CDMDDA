package com.example.cdmdda.data.dto

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import java.util.*

data class SearchQuery(
    val query: String = String(),
    @get:PropertyName("queried_on") @set:PropertyName("queried_on")
    var queriedOn: Timestamp = Timestamp(Date()),
)
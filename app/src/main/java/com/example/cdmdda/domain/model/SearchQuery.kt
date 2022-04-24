package com.example.cdmdda.domain.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import java.util.*

data class SearchQuery constructor(
    val query: String = String(),
    @get:PropertyName("queried_on") @set:PropertyName("queried_on")
    var queriedOn: Timestamp = Timestamp(Date()),
)
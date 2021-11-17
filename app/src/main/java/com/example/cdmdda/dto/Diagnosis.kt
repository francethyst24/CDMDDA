package com.example.cdmdda.dto

import com.google.firebase.Timestamp

data class Diagnosis (
    var name: String? = null,
    var user_id: String? = null,
    var diagnosed_on: Timestamp? = null,
)
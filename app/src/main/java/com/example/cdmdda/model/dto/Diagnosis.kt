package com.example.cdmdda.model.dto

import com.google.firebase.Timestamp
import java.util.*


data class Diagnosis (
    val name : String = String(),
    val user_id : String = String(),
    val diagnosed_on : Timestamp = Timestamp(Date()),
)
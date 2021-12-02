package com.example.cdmdda.model.dto

import com.google.firebase.Timestamp
import java.util.Date


data class Diagnosis (
    val name : String = "",
    val user_id : String = "",
    val diagnosed_on : Timestamp = Timestamp(Date()),
)
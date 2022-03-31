package com.example.cdmdda.common

import java.text.SimpleDateFormat
import java.util.*

object DateTimeFormat {
    const val DATE_FORMAT = "MMM dd yyyy"
    const val TIME_FORMAT = "HH:mm"

    fun Date.formatDate(string: String): String = SimpleDateFormat(string, Locale.getDefault()).format(this)
}
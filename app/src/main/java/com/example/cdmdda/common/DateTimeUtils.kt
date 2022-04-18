package com.example.cdmdda.common

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {
    const val DATE_FORMAT = "MMM dd yyyy"
    const val TIME_FORMAT = "HH:mm"

    fun Date.toStringWith(format: String): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(this)
    }
}
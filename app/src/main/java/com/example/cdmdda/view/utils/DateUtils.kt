package com.example.cdmdda.view.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    fun formatDate(string: String, date: Date) : String = SimpleDateFormat(string, Locale.getDefault()).format(date)
}
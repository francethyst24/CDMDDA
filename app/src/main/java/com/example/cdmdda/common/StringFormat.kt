package com.example.cdmdda.common

import java.util.*

object StringFormat {
    fun String.capitalize(): String {
        return split(Regex("\\s+")).joinToString(" ") {
            it.replaceFirstChar { firstChar ->
                if (firstChar.isLowerCase()) {
                    firstChar.titlecase(Locale.getDefault())
                } else { firstChar.toString() }
            }
        }
    }

    fun String.toResourceId() = lowercase().replace(" ", "_")
}


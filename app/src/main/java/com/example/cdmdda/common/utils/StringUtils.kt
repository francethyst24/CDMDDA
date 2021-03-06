package com.example.cdmdda.common.utils

import java.text.SimpleDateFormat
import java.util.*

object StringUtils {
    // RegEx
    const val CAPITAL = "[A-Z]"
    const val NUMERAL = "[0-9]"
    const val WHITESPACE = " "
    // const val SPECIAL = "[^a-zA-Z0-9 ]"

    const val DATE_FORMAT = "MMM dd yyyy"
    const val TIME_FORMAT = "HH:mm"

    fun Date.toStringWith(format: String): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(this)
    }

    fun String?.capitalize(): String {
        return this?.let {
            split(Regex("\\s+")).joinToString(" ") {
                it.replaceFirstChar { firstChar ->
                    if (firstChar.isLowerCase()) {
                        firstChar.titlecase(Locale.getDefault())
                    } else {
                        firstChar.toString()
                    }
                }
            }
        } ?: run { String() }
    }

    val String.snakecase get() = lowercase().replace(" ", "_")

    val String.wordIndices get() = 0..count { it == " ".single() }

    fun String.wordAt(index: Int) = split(Regex(" "), index.inc()).last()

    fun String.equalsAny(others: Collection<String>) = others.contains(this)

}


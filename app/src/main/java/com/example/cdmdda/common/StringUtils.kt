package com.example.cdmdda.common

import java.util.*

object StringUtils {
    fun String?.capitalize(): String {
        return this?.let {
            split(Regex("\\s+")).joinToString(" ") {
                it.replaceFirstChar { firstChar ->
                    if (firstChar.isLowerCase()) {
                        firstChar.titlecase(Locale.getDefault())
                    } else { firstChar.toString() }
                }
            }
        }?: run { String() }
    }

    val String.snakecase get() = lowercase().replace(" ", "_")

    val String.wordIndices get() = 0..count { it == " ".single() }

    fun String.wordAt(index: Int) = split(Regex(" "), index.inc()).last()

}


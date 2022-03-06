package com.example.cdmdda.view.utils

import android.content.Context
import android.content.Intent
import android.view.View
import java.util.*

object StringUtils {
    fun attachListeners(context: Context, itemIds: List<String>, itemTexts: List<String> = listOf())
            : List<Pair<String, View.OnClickListener>> {
        if (itemTexts.isEmpty()) return attachListenersDefault(context, itemIds)
        val pairs = mutableListOf<Pair<String, View.OnClickListener>>()
        itemIds.zip(itemTexts).forEach { (itemId, itemText) ->
            pairs.add(itemText to View.OnClickListener {
                val displayIntent = IntentUtils.interactivityIntent(context, itemId)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(displayIntent)
                //(context as Activity).finish()
            })
        }
        return pairs
    }

    private fun attachListenersDefault(context: Context, itemIds: List<String>)
            : List<Pair<String, View.OnClickListener>> {
        val pairs = mutableListOf<Pair<String, View.OnClickListener>>()
        for (itemId in itemIds) {
            pairs.add(itemId to View.OnClickListener {
                val displayIntent = IntentUtils.interactivityIntent(context, itemId)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(displayIntent)
                //(context as Activity).finish()
            })
        }
        return pairs
    }

}

fun String.toResourceId(): String {
    return lowercase().replace(" ", "_")
}

fun String.capitalize(): String {
    return split(Regex("\\s+")).joinToString(" ") {
        it.replaceFirstChar { firstChar ->
            if (firstChar.isLowerCase()) {
                firstChar.titlecase(Locale.getDefault())
            } else { firstChar.toString() }
        }
    }
}
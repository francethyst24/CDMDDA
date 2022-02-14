package com.example.cdmdda.view.utils

import android.content.Context
import android.content.Intent
import android.view.View

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

    fun toResourceId(string: String) : String {
        return string.lowercase().replace(" ", "_")
    }

}
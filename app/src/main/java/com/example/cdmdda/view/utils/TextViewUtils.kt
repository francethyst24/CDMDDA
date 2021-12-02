package com.example.cdmdda.view.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.example.cdmdda.view.BaseCompatActivity
import com.example.cdmdda.view.DisplayCropActivity
import com.example.cdmdda.view.DisplayDiseaseActivity
import java.text.SimpleDateFormat
import java.util.*

class TextViewUtils {
    companion object {
        fun generateLinks(textView: TextView, padding : Int = -1, vararg links: Pair<String, View.OnClickListener>) {
            val spannableString = SpannableString(textView.text)
            var start = padding // 9

            for (link in links) {
                val clickableSpan = object : ClickableSpan() {
                    override fun updateDrawState(ds: TextPaint) {
                        ds.apply { color = linkColor; isUnderlineText = true }
                    }

                    override fun onClick(widget: View) {
                        Selection.setSelection((widget as TextView).text as Spannable, 0)
                        widget.invalidate()
                        link.second.onClick(widget)
                    }
                }

                start = textView.text.toString().indexOf(link.first, start + 1)
                if (start == -1) continue
                spannableString.setSpan(
                    clickableSpan, start, start + link.first.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            textView.movementMethod = LinkMovementMethod.getInstance()
            textView.setText(spannableString, TextView.BufferType.SPANNABLE)
        }

        fun attachListeners(
            context: Context,
            itemIds: List<String>,
            itemTexts: List<String> = listOf()
        ) : List<Pair<String, View.OnClickListener>> {
            if (itemTexts.isEmpty()) return attachListenersDefault(context, itemIds)
            val pairs = mutableListOf<Pair<String, View.OnClickListener>>()
            itemIds.zip(itemTexts).forEach { (itemId, itemText) ->
                pairs.add(itemText to View.OnClickListener {
                    var displayIntent = when (context) {
                        is DisplayDiseaseActivity -> {
                            Intent(context, DisplayCropActivity::class.java)
                                .putExtra("crop_id", itemId)
                        }
                        is DisplayCropActivity -> {
                            Intent(context, DisplayDiseaseActivity::class.java)
                                .putExtra("disease_id", itemId)
                        }
                        else -> (context as Activity).intent
                    }
                    context.startActivity(displayIntent)
                    (context as Activity).finish()
                })
            }
            return pairs
        }

        private fun attachListenersDefault(context: Context, itemIds: List<String>)
            : List<Pair<String, View.OnClickListener>> {
            val pairs = mutableListOf<Pair<String, View.OnClickListener>>()
            for (itemId in itemIds) {
                pairs.add(itemId to View.OnClickListener {
                    var displayIntent = when (context) {
                        is DisplayDiseaseActivity -> {
                            Intent(context, DisplayCropActivity::class.java)
                                .putExtra("crop_id", itemId)
                        }
                        is DisplayCropActivity -> {
                            Intent(context, DisplayDiseaseActivity::class.java)
                                .putExtra("disease_id", itemId)
                        }
                        else -> (context as Activity).intent
                    }
                    context.startActivity(displayIntent)
                    (context as Activity).finish()
                })
            }
            return pairs
        }

    }
}

class DateUtils {
    companion object {
        fun formatDate(string: String, date: Date) : String = SimpleDateFormat(string, Locale.getDefault()).format(date)
    }
}
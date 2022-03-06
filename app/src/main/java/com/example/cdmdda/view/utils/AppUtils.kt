package com.example.cdmdda.view.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import com.example.cdmdda.view.DisplayCropActivity
import com.example.cdmdda.view.DisplayDiseaseActivity
import com.example.cdmdda.view.SearchableActivity
import java.text.SimpleDateFormat
import java.util.*

fun Context.interactivityIntent(id: String) : Intent = when (this) {
    is DisplayDiseaseActivity ->
        Intent(this, DisplayCropActivity::class.java).putExtra("crop_id", id)
    is DisplayCropActivity, is SearchableActivity ->
        Intent(this, DisplayDiseaseActivity::class.java).putExtra("disease_id", id)
    else ->
        (this as Activity).intent
}

fun TextView.generateLinks(padding: Int = -1, vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(text)
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

        start = text.toString().indexOf(link.first, start + 1)
        if (start == -1) continue
        spannableString.setSpan(
            clickableSpan, start, start + link.first.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    movementMethod = LinkMovementMethod.getInstance()
    setText(spannableString, TextView.BufferType.SPANNABLE)
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

fun Date.formatDate(string: String) : String = SimpleDateFormat(string, Locale.getDefault()).format(this)
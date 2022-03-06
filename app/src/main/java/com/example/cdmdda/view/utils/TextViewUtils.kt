package com.example.cdmdda.view.utils

import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView

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
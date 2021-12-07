package com.example.cdmdda.view.utils

import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView

object TextViewUtils {
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

}


package com.example.cdmdda.common.utils

import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.text.getSpans
import com.example.cdmdda.R
import com.example.cdmdda.presentation.adapter.LinkAdapter
import com.google.android.material.color.MaterialColors

object TextViewUtils {
    fun getPressedTextColor(view: View): Int {
        return MaterialColors.getColor(view, R.attr.colorPrimary)
    }

    fun TextView.setLinkText(
        fullText: String,
        clickableText: String,
        listener: View.OnClickListener,
    ) {
        val spannableString = SpannableString(fullText)
        val clickableSpan = object : TouchableSpan(getPressedTextColor(this)) {
            override fun onClick(widget: View) = listener.onClick(widget)
        }
        val startIndex = fullText.indexOf(clickableText)
        val endIndex = startIndex + clickableText.length

        spannableString.setSpan(
            clickableSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        movementMethod = LinkTouchMovementMethod
        setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    abstract class TouchableSpan(
        @ColorInt private val pressedTextColor: Int,
    ) : ClickableSpan() {
        var isPressed: Boolean = false

        override fun updateDrawState(ds: TextPaint) {
            ds.color = if (isPressed) pressedTextColor else ds.linkColor
            ds.isUnderlineText = isPressed
        }
    }

    object LinkTouchMovementMethod : LinkMovementMethod() {
        private var pressedSpan: TouchableSpan? = null

        @Suppress("NAME_SHADOWING")
        override fun onTouchEvent(widget: TextView?, buffer: Spannable?, event: MotionEvent?): Boolean {
            widget?.let { widget ->
                buffer?.let { buffer ->
                    event?.let { event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                pressedSpan = getPressedSpan(widget, buffer, event)
                                pressedSpan?.let {
                                    it.isPressed = true
                                    Selection.setSelection(buffer, buffer.getSpanStart(it), buffer.getSpanEnd(it))
                                }
                            }
                            MotionEvent.ACTION_MOVE -> {
                                val touchedSpan = getPressedSpan(widget, buffer, event)
                                pressedSpan?.let {
                                    if (touchedSpan != it) {
                                        it.isPressed = false
                                        pressedSpan = null
                                        Selection.removeSelection(buffer)
                                    }
                                }
                            }
                            else -> {
                                pressedSpan?.let {
                                    it.isPressed = false
                                    super.onTouchEvent(widget, buffer, event)
                                }
                                pressedSpan = null
                                Selection.removeSelection(buffer)
                            }
                        }
                    }
                }
            }
            return true
        }

        private fun getPressedSpan(widget: TextView, buffer: Spannable, event: MotionEvent): TouchableSpan? {
            val x = event.x - widget.totalPaddingLeft + widget.scrollX
            val y = event.y.toInt() - widget.totalPaddingTop + widget.scrollY
            val layout = widget.layout
            val position = layout.getOffsetForHorizontal(layout.getLineForVertical(y), x)
            val link = buffer.getSpans<TouchableSpan>(position, position)
            return if (link.isNotEmpty() && withinBounds(position, buffer, link.first())) {
                link.first()
            } else {
                null
            }
        }

        private fun withinBounds(position: Int, buffer: Spannable, tag: Any): Boolean {
            return position >= buffer.getSpanStart(tag) && position <= buffer.getSpanEnd(tag)
        }
    }
}
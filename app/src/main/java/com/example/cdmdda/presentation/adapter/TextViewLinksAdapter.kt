package com.example.cdmdda.presentation.adapter

import android.graphics.Color
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.text.getSpans
import com.example.cdmdda.R
import com.example.cdmdda.data.dto.TextUiState
import com.google.android.material.color.MaterialColors


class TextViewLinksAdapter(
    private val dataset: List<TextUiState>,
    private val padding: Int = -1,
    private val onTextClick: (TextUiState) -> Unit,
) {
    companion object {
        fun TextView.setAdapter(adapter: TextViewLinksAdapter) = adapter.setClickableText(this)
    }

    fun setClickableText(view: TextView) {
        val spannableString = SpannableString(view.text)
        var start = padding // 9

        for (item in dataset) {
            val pressedTextColor = MaterialColors.getColor(view, R.attr.colorPrimary)
            val touchableSpan = object : TouchableSpan(pressedTextColor) {
                override fun onClick(widget: View) {
                    Selection.setSelection((widget as TextView).text as Spannable, 0)
                    widget.invalidate()
                    onTextClick(item)
                }
            }
            val displayText = item.displayName(view.context)
            start = view.text.toString().indexOf(displayText, start + 1)
            if (start == -1) continue

            val flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            spannableString.setSpan(touchableSpan, start, start + displayText.length, flag)
        }
        view.highlightColor = Color.TRANSPARENT
        view.movementMethod = LinkTouchMovementMethod/*.getInstance()*/
        view.setText(spannableString, TextView.BufferType.SPANNABLE)
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
            } else null
        }

        private fun withinBounds(position: Int, buffer: Spannable, tag: Any): Boolean {
            return position >= buffer.getSpanStart(tag) && position <= buffer.getSpanEnd(tag)
        }
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
}

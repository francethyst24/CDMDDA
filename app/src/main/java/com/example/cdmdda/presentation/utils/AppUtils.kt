package com.example.cdmdda.presentation.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Parcelable
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.example.cdmdda.R
import com.example.cdmdda.common.AppData
import com.example.cdmdda.data.dto.CropTextUiState
import com.example.cdmdda.data.dto.DiseaseTextUiState
import com.example.cdmdda.data.dto.TextUiState
import com.example.cdmdda.presentation.CropProfileActivity
import com.example.cdmdda.presentation.DiseaseProfileActivity
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

fun Context.interactivity(type: String? = null, parcel: Parcelable? = null): Intent = when(type) {
    AppData.CROP -> Intent(this, CropProfileActivity::class.java).putExtra(type, parcel)
    AppData.DISEASE -> Intent(this, DiseaseProfileActivity::class.java).putExtra(type, parcel)
    else -> (this as Activity).intent
}

fun TextView.generateLinks(padding: Int = -1, vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(text)
    var start = padding // 9

    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(ds: TextPaint) {
                ds.apply {
                    color = linkColor
                    isUnderlineText = true
                }
            }

            override fun onClick(widget: View) {
                Selection.setSelection((widget as TextView).text as Spannable, 0)
                widget.invalidate()
                link.second.onClick(widget)
            }
        }
        this.highlightColor = Color.TRANSPARENT
        start = text.toString().indexOf(link.first, start + 1)
        if (start == -1) continue
        spannableString.setSpan(
            clickableSpan,
            start,
            start + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
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

fun TextInputLayout.toggleTextVisibility(editText: EditText): PasswordTransformationMethod? {
    editText.let {
        val condition = it.transformationMethod != null
        setEndIconDrawable(if (condition) {
            R.drawable.ic_baseline_visibility_24
        } else {
            R.drawable.ic_baseline_visibility_off_24
        })
        return if (condition) null else PasswordTransformationMethod()
    }
}

fun Context.attachListeners(type: String, ids: List<TextUiState>): List<Pair<String, View.OnClickListener>> = when (ids.first()) {
    is CropTextUiState -> {
        val pairs = mutableListOf<Pair<String, View.OnClickListener>>()
        ids.forEach { parcel ->
            val cropParcel = parcel as CropTextUiState
            pairs.add(cropParcel.name to View.OnClickListener {
                val displayIntent = interactivity(type, cropParcel)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(displayIntent)
            })
        }
        pairs
    }
    is DiseaseTextUiState -> {
        val pairs = mutableListOf<Pair<String, View.OnClickListener>>()
        ids.forEach { parcel ->
            val diseaseParcel = parcel as DiseaseTextUiState
            pairs.add(diseaseParcel.id to View.OnClickListener {
                val displayIntent = interactivity(type, diseaseParcel)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(displayIntent)
            })
        }
        pairs
    }

}

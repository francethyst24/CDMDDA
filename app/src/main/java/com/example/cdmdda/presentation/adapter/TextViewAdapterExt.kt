package com.example.cdmdda.presentation.adapter

import android.widget.TextView

fun TextView.setAdapter(adapter: TextViewLinksAdapter) = adapter.setClickableText(this)
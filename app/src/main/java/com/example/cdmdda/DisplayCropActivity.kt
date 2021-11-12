package com.example.cdmdda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.cdmdda.databinding.ActivityDisplayCropBinding
import com.example.cdmdda.viewModelFactories.IdViewModelFactory
import com.example.cdmdda.viewmodels.DisplayCropViewModel

class DisplayCropActivity : AppCompatActivity() {

    // declare: ViewBinding, ViewModel
    private lateinit var binding: ActivityDisplayCropBinding
    private lateinit var viewModel: DisplayCropViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cropId = intent.getStringExtra("crop_id")!!

        // region -- init: ViewBinding, ViewModel
        binding = ActivityDisplayCropBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this@DisplayCropActivity,
            IdViewModelFactory(application, cropId)
        ).get(DisplayCropViewModel::class.java)

        val toolbar = binding.toolbarDisplayCrop
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            viewModel.crop.observe(this@DisplayCropActivity) { title = it.name }
        }
        setContentView(binding.root)
        // endregion

        viewModel.crop.observe(this@DisplayCropActivity) {
            binding.apply {
                textDisplayCropName.text = it.name
                textDisplayCropSciName.text = it.sci_name
                val diseasesText = getString(R.string.text_diseases) + it.diseases.joinToString()
                textDiseases.text = diseasesText

                val pairedList = it.diseases.attachOnClickListeners()
                textDiseases.generateLinks(
                    getString(R.string.text_diseases).length - 1, *pairedList.toTypedArray()
                )
            }
        }


    }

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            this@DisplayCropActivity.finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    fun TextView.generateLinks(padding : Int = -1, vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(this@generateLinks.text)
        var start = padding // 9

        for (link in links) {
            println(link.first)
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

            start = this@generateLinks.text.toString().indexOf(link.first, start + 1)
            if (start == -1) continue
            println("setSpan(start:$start, end:"+ (start + link.first.length))
            spannableString.setSpan(
                clickableSpan, start, start + link.first.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this@generateLinks.movementMethod = LinkMovementMethod.getInstance()
        this@generateLinks.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    fun List<String>.attachOnClickListeners() : List<Pair<String, View.OnClickListener>> {
        val finalList = mutableListOf<Pair<String, View.OnClickListener>>()

        for (string in this@attachOnClickListeners) {
            finalList.add(Pair(string, View.OnClickListener {
                Toast.makeText(applicationContext, "$string clicked", Toast.LENGTH_SHORT).show()
            }))
        }

        return finalList
    }
}
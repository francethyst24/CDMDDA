package com.example.cdmdda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
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

}
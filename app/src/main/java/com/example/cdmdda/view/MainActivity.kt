package com.example.cdmdda.view

import android.Manifest
import android.app.SearchManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.R
import com.example.cdmdda.databinding.ActivityMainBinding
import com.example.cdmdda.model.dto.CropItem
import com.example.cdmdda.model.dto.Diagnosis
import com.example.cdmdda.view.adapter.CropItemAdapter
import com.example.cdmdda.view.adapter.DiagnosisFirestoreAdapter
import com.example.cdmdda.view.fragment.DiagnosisFailureDialog
import com.example.cdmdda.view.fragment.EmailVerificationDialog
import com.example.cdmdda.view.fragment.LogoutDialog
import com.example.cdmdda.viewmodel.MainViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase

class MainActivity : BaseCompatActivity(), // region // interface: Adapters, Dialogs
    DiagnosisFirestoreAdapter.DiagnosisItemEventListener,
    CropItemAdapter.CropItemEventListener,
    LogoutDialog.LogoutDialogListener,
    EmailVerificationDialog.EmailVerificationDialogListener
    // endregion
{
    // region // declare: ViewBinding, ViewModel
    private lateinit var layout: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    // endregion

    private val auth = Firebase.auth

    private var diagnosisFirestoreAdapter: DiagnosisFirestoreAdapter? = null
    private var cropAdapter: CropItemAdapter? = null

    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // region // init: ViewBinding, ViewModel, Toolbar
        layout = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setSupportActionBar(layout.toolbarMain)
        setContentView(layout.root)
        // endregion

//bell was here

        // region // events: Button
        layout.buttonCamera.setOnClickListener {
            val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                cameraPermission.launch(Manifest.permission.CAMERA)
            } else {
                cameraLauncher.launch(null)
            }
        }

        layout.buttonGallery.setOnClickListener {
            val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (permission != PackageManager.PERMISSION_GRANTED) {
                galleryPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                galleryLauncher.launch("image/*")
            }
        }

        layout.buttonCancelInference.setOnClickListener {
            viewModel.cancelInference()
            updateUIOnInference(View.INVISIBLE)
        }
        // endregion

        // init: RecyclerView
        val allCrops = resources.getStringArray(R.array.string_unsupported_crops).toList()
        setCropRecyclerView(viewModel.cropItemList)
        viewModel.cropItems(this, allCrops).observe(this) {
            cropAdapter?.notifyItemInserted(it)
            Log.i("MainActivity", "notifyItemInserted($it), cropItemList.size=${viewModel.cropItemList.size}")
        }

        layout.textWelcome.text = getString(R.string.ui_text_main)
            .plus(" ")
            .plus(auth.currentUser?.email ?: getString(R.string.ui_user_guest))
        if (viewModel.isUserAuthenticated()) {
            setDiagnosisRecyclerView()
            layout.textTitleHistory.visibility = View.VISIBLE
            // email verification
            auth.currentUser?.let {
                it.reload()
                if (!it.isEmailVerified) {
                    EmailVerificationDialog(it).show(supportFragmentManager, "EmailVerificationDialog")
                }
            }
        }
    }

    // region // init: RecyclerView
    private fun setCropRecyclerView(cropList: List<CropItem>) {
        cropAdapter = CropItemAdapter(cropList, resources.getString(R.string.var_dataset)).also {
            it.setOnItemClickListener(this)
        }
        layout.recyclerCrops.also {
            it.setHasFixedSize(false)
            val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
            getDrawable(R.drawable.divider_vertical)?.let { drawable ->
                divider.setDrawable(drawable)
            }
            it.addItemDecoration(divider)
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = cropAdapter
        }
    }

    private fun setDiagnosisRecyclerView() {
        layout.recyclerDiagnosis.visibility = View.VISIBLE
        val diagnosisRecyclerOptions = FirestoreRecyclerOptions.Builder<Diagnosis>()
            .setQuery(viewModel.diagnosisQuery, Diagnosis::class.java)
            .setLifecycleOwner(this)
            .build()
        diagnosisFirestoreAdapter = DiagnosisFirestoreAdapter(diagnosisRecyclerOptions).also {
            it.setOnItemClickListener(this)
        }
        layout.recyclerDiagnosis.also {
            it.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val divider = DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL)
            getDrawable(R.drawable.divider_horizontal)?.let { drawable ->
                divider.setDrawable(drawable)
            }
            it.addItemDecoration(divider)
            it.itemAnimator = null
            it.adapter = diagnosisFirestoreAdapter
        }
    }
    // endregion

    // region // events: RecyclerView.Item
    override fun onCropItemClick(cropId: String) {
        val displayCropIntent = Intent(this, DisplayCropActivity::class.java)
        displayCropIntent.putExtra("crop_id", cropId)
        startActivity(displayCropIntent)
    }

    override fun onDiagnosisItemClick(documentSnapshot: DocumentSnapshot, position: Int) {
        val displayDiseaseIntent = Intent(this, DisplayDiseaseActivity::class.java)
        displayDiseaseIntent.putExtra("disease_id", documentSnapshot.getString("name"))
        startActivity(displayDiseaseIntent)
    }

    override fun onEmptyListReturned() {
        layout.textNoDiagnosis.visibility = View.VISIBLE
    }
    // endregion

    // events: LogoutDialog
    override fun onLogoutClick() {
        auth.signOut()
        finish(); startActivity(Intent(intent))
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
    }

    // events: EmailVerificationDialog
    override fun onLogoutInsteadClick() {
        this.onLogoutClick()
    }

    // events: MenuItem
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_account -> {
            if (auth.currentUser != null) {
                LogoutDialog().show(supportFragmentManager, "LogoutDialog")
            } else {
                startActivity(Intent(this, AccountActivity::class.java))
            }
            true
        }
        R.id.action_settings -> {
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    // inflate: menu -> actionBar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        // region // implement: Search(SystemService, SearchManager, ComponentName)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        val componentName = ComponentName(this, SearchableActivity::class.java)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        // endregion

        // region // fixes: search UI
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                layout.maskMain.visibility = View.VISIBLE

                // close searchView: on background click
                layout.maskMain.setOnClickListener { searchItem.collapseActionView() }

                // hide other:MenuItem on expand:Search
                (menu.findItem(R.id.action_settings)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                (menu.findItem(R.id.action_account)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                return true
            }

            // undo expand:Search UI changes
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                layout.maskMain.visibility = View.GONE
                this@MainActivity.invalidateOptionsMenu()
                return true
            }

        })

        searchView.findViewById<EditText>(R.id.search_src_text).also {
            // necessary: default MaterialTheme.SearchView(Day) is ugly
            val currentTheme = it.context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
            if (currentTheme != Configuration.UI_MODE_NIGHT_YES) {
                it.setHintTextColor(ContextCompat.getColor(this, R.color.material_on_primary_disabled))
                it.setTextColor(ContextCompat.getColor(this, R.color.white))
            }
            // close searchView: on soft keyboard down
            it.setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) searchItem.collapseActionView()
                Log.d("IGNORE", "Logging param to curb warnings $v")
            }
        }
        // endregion

        return super.onCreateOptionsMenu(menu)
    }

    // region // launcher: Camera
    private val cameraPermission = registerForActivityResult(RequestPermission()) { isPermitted ->
        if (isPermitted) layout.buttonCamera.callOnClick()
    }

    private val cameraLauncher = registerForActivityResult(TakePicturePreview()) { bitmap ->
        if (bitmap == null) return@registerForActivityResult
        runInference(bitmap)
    }
    // endregion

    // region // launcher: Gallery
    private val galleryPermission = registerForActivityResult(RequestPermission()) { isPermitted ->
        if (isPermitted) layout.buttonGallery.callOnClick()
    }

    private val galleryLauncher = registerForActivityResult(GetContent()) { uri ->
        if (uri == null) return@registerForActivityResult
        runInference(uri)
    }
    // endregion

    // region // ml: Any(Bitmap or Uri) -> viewModel -> DisplayDiseaseActivity
    private fun runInference(any: Any) {
        updateUIOnInference(View.VISIBLE)
        viewModel.runInference(any).observe(this) { result ->
            onInferenceResult(result)
        }
    }

    private fun onInferenceResult(diseaseId: String) {
        updateUIOnInference(View.INVISIBLE)
        if (diseaseId == "null") {
            DiagnosisFailureDialog().show(supportFragmentManager, "DiagnosisFailureDialog")
            return
        }
        if (auth.currentUser != null) viewModel.saveDiagnosis(diseaseId)
        val displayDiseaseIntent = Intent(this, DisplayDiseaseActivity::class.java)
        displayDiseaseIntent.putExtra("disease_id", diseaseId)
        startActivity(displayDiseaseIntent)
    }

    private fun updateUIOnInference(visibility: Int) = when (visibility) {
        View.GONE, View.INVISIBLE -> {
            layout.apply {
                maskMain.visibility = View.GONE
                loadingInference.visibility = View.INVISIBLE
                divInference.visibility = View.GONE
            }
        }
        else -> {
            layout.apply {
                maskMain.visibility = View.VISIBLE
                loadingInference.visibility = View.VISIBLE
                textAnalyzing.visibility = View.VISIBLE
                divInference.visibility = View.VISIBLE
            }
        }
    }

    // endregion

}
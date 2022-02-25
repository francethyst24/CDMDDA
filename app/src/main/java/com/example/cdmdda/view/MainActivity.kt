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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.R
import com.example.cdmdda.databinding.ActivityMainBinding
import com.example.cdmdda.view.adapter.CropItemAdapter
import com.example.cdmdda.view.adapter.DiagnosisFirestoreAdapter
import com.example.cdmdda.view.fragment.DiagnosisFailureDialog
import com.example.cdmdda.view.fragment.LogoutDialog
import com.example.cdmdda.viewmodel.MainViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase

class MainActivity : BaseCompatActivity(), LogoutDialog.LogoutDialogListener, DiagnosisFirestoreAdapter.DiagnosisEventListener, CropItemAdapter.CropItemEventListener {

    // region // declare: ViewBinding, ViewModel
    private lateinit var layout: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    // endregion
    // init: Firebase(Auth)
    private val auth = Firebase.auth

    // region // declare: FirestoreRecyclerAdapter
    private var diagnosisFirestoreAdapter: DiagnosisFirestoreAdapter? = null

    // endregion
    // declare: SearchView
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
        setCropRecyclerView()
        if (layout.textUserId.text != getString(R.string.text_guest)) {
            layout.recyclerDiagnosis.visibility = View.INVISIBLE
            setDiagnosisRecyclerView()
        }
    }

    private fun setCropRecyclerView() {
        val cropAdapter = CropItemAdapter(
            resources.getStringArray(R.array.string_unsupported_crops).toList(),
            resources.getString(R.string.dataset)
        )
        layout.recyclerCrops1.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = cropAdapter
        }
        cropAdapter.setOnItemClickListener(this@MainActivity)
    }


    // region // init: RecyclerView

    private fun setDiagnosisRecyclerView() {
        layout.recyclerDiagnosis.visibility = View.VISIBLE
        diagnosisFirestoreAdapter = DiagnosisFirestoreAdapter(viewModel.diagnosisRecyclerOptions)
        layout.recyclerDiagnosis.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            itemAnimator = null
            adapter = diagnosisFirestoreAdapter
        }
        diagnosisFirestoreAdapter?.setOnItemClickListener(this)
    }
    // endregion

    // region // lifecycle
    override fun onStart() {
        super.onStart()
        // START data flow

        // region // update: UI -> authStateChanged
        layout.textUserId.text = if (viewModel.getUserDiagnosisHistory()) {
            setDiagnosisRecyclerView()
            diagnosisFirestoreAdapter?.startListening()
            auth.currentUser?.email
        } else {
            getString(R.string.text_guest)
        }
        // endregion

    }

    override fun onStop() {
        super.onStop()
        // region // STOP data flow - avoid memory leaks
        diagnosisFirestoreAdapter?.stopListening()
        // endregion
    }
    // endregion

    // region // events: RecyclerView.Item
    override fun onCropItemClick(cropId: String) {
        val displayCropIntent = Intent(this@MainActivity, DisplayCropActivity::class.java)
        displayCropIntent.putExtra("crop_id", cropId)
        startActivity(displayCropIntent)
    }

    override fun onDiagnosisItemClick(documentSnapshot: DocumentSnapshot, position: Int) {
        val displayDiseaseIntent = Intent(this@MainActivity, DisplayDiseaseActivity::class.java)
        displayDiseaseIntent.putExtra("disease_id", documentSnapshot.getString("name"))
        startActivity(displayDiseaseIntent)
    }

    // endregion

    // region // events: LogoutDialog
    override fun onLogoutClick() {
        auth.signOut()
        finish(); startActivity(Intent(intent))
        Toast.makeText(this@MainActivity, "Logged out", Toast.LENGTH_SHORT).show()
    }
    // endregion

    // events: MenuItem
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_account -> {
            if (auth.currentUser == null) {
                startActivity(Intent(this@MainActivity, AccountActivity::class.java))
            } else {
                LogoutDialog().show(supportFragmentManager, "LogoutDialog")
            }
            true
        }
        R.id.action_settings -> {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    // inflate: menu -> actionBar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        // region // implement: Search(SystemService, SearchManager, ComponentName)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        val componentName = ComponentName(this@MainActivity, SearchableActivity::class.java)
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

        searchView.findViewById<EditText>(R.id.search_src_text).apply {
            // necessary: default MaterialTheme.SearchView(Day) is ugly
            val currentTheme = context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
            if (currentTheme != Configuration.UI_MODE_NIGHT_YES) {
                setHintTextColor(ContextCompat.getColor(this@MainActivity, R.color.material_on_primary_disabled))
                setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
            }
            // close searchView: on soft keyboard down
            setOnFocusChangeListener { v, hasFocus ->
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
        viewModel.runInference(any).observe(this@MainActivity) { result ->
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
        val displayDiseaseIntent = Intent(this@MainActivity, DisplayDiseaseActivity::class.java)
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
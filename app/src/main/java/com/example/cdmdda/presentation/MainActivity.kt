package com.example.cdmdda.presentation

import android.Manifest
import android.app.SearchManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.provider.SearchRecentSuggestions
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.R
import com.example.cdmdda.common.AppData
import com.example.cdmdda.data.SuggestionsProvider
import com.example.cdmdda.data.dto.DiseaseText
import com.example.cdmdda.data.dto.UserInput
import com.example.cdmdda.databinding.ActivityMainBinding
import com.example.cdmdda.domain.usecase.*
import com.example.cdmdda.presentation.adapter.CropItemAdapter
import com.example.cdmdda.presentation.adapter.DiagnosisFirestoreAdapter
import com.example.cdmdda.presentation.fragment.DiagnosisFailDialog
import com.example.cdmdda.presentation.fragment.EmailVerificationDialog
import com.example.cdmdda.presentation.fragment.LogoutDialog
import com.example.cdmdda.presentation.helper.CropResourceHelper
import com.example.cdmdda.presentation.helper.ResourceHelper
import com.example.cdmdda.presentation.viewmodel.MainViewModel
import com.example.cdmdda.presentation.viewmodel.factory.CreateWithFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class MainActivity : BaseCompatActivity() {
    companion object { const val TAG = "MainActivity" }
    private val cropResources: CropResourceHelper by lazy { CropResourceHelper(this) }
    // region // declare: ViewBinding, ViewModel
    private val layout: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val viewModel: MainViewModel by viewModels {
        CreateWithFactory {
            MainViewModel(
                DiagnoseDiseaseUseCase(PrepareBitmapUseCase(), GetInferenceMLUseCase()),
                GetDiagnosisHistoryUseCase(),
                GetAuthStateUseCase(),
                GetCropItemUseCase(cropResources),
            )
        }
    }
    // endregion

    private var diagnosisFirestoreAdapter: DiagnosisFirestoreAdapter? = null
    private val cropItemAdapter: CropItemAdapter by lazy {
        CropItemAdapter(viewModel.cropList) {
            // User Event: Click CropRecycler ItemHolder
            startActivity(interactivity(AppData.CROP, it))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(layout.toolbarMain)


//bell was here

        // User Event : Click Camera Button
        layout.buttonCamera.setOnClickListener {
            val requirements = Manifest.permission.CAMERA
            val isPermitted = ContextCompat.checkSelfPermission(this, requirements)
            if (isPermitted != PackageManager.PERMISSION_GRANTED) {
                cameraPermission.launch(requirements)
            } else {
                cameraActivity.launch(null)
            }
        }

        // User Event : Click Gallery Button
        layout.buttonGallery.setOnClickListener {
            val requirements = Manifest.permission.READ_EXTERNAL_STORAGE
            val isPermitted = ContextCompat.checkSelfPermission(this, requirements)
            if (isPermitted != PackageManager.PERMISSION_GRANTED) {
                galleryPermission.launch(requirements)
            } else {
                galleryActivity.launch("image/*")
            }
        }

        // User Event : Click Cancel Button
        // NOTE: Shown via "Business Logic: Inference"
        layout.buttonCancelInference.setOnClickListener {
            //viewModel.cancelInference()
            viewModel.cancelDiagnosisAsync()
            layout.updateOnInference(View.INVISIBLE)
        }

        // UI Event: RecyclerView
        setCropRecyclerView()

        // UI Event: RecyclerView: Update data
        val resourceHelper = ResourceHelper(this)
        viewModel.cropCount(resourceHelper).observe(this) {
            cropItemAdapter.notifyItemInserted(it)
        }

        // UI Event: TextView: Display, if User exists
        val displayName = viewModel.currentUser?.email ?: getString(R.string.ui_user_guest)
        layout.textWelcome.text = getString(R.string.ui_text_main).plus(" $displayName")

        // Multithreading: FirebaseUser
        lifecycleScope.launchWhenStarted {
            val auth = SuggestionsProvider.AUTHORITY
            val mode = SuggestionsProvider.MODE
            val provider = SearchRecentSuggestions(this@MainActivity, auth, mode)
            if (viewModel.isUserAuthenticated(provider)) {
                // UI Event: RecyclerView, if User exists
                setDiagnosisRecyclerView()
                // UI Event : Dialog: if User NOT Verified
                setEmailVerificationDialog()
            }
        }

        setContentView(layout.root)
    }

    // UI Event: EmailVerificationDialog
    private fun setEmailVerificationDialog() {
        viewModel.currentUser?.let { it ->
            it.reload().addOnCompleteListener { _ ->
                if (!it.isEmailVerified) {
                    val dialog = EmailVerificationDialog(it, onPositiveButtonClick =  { logout() })
                    dialog.show(supportFragmentManager, EmailVerificationDialog.TAG)
                }
            }
        }
    }

    // UI Event: RecyclerView - Crops
    private fun setCropRecyclerView() {
        layout.recyclerCrops.also {
            it.setHasFixedSize(true)
            val divider = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
            getDrawable(R.drawable.divider_vertical)?.let { drawable ->
                divider.setDrawable(drawable)
            }
            it.addItemDecoration(divider)
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = cropItemAdapter
        }
    }

    // UI Event: RecyclerView - Diagnosis
    private fun setDiagnosisRecyclerView() {
        layout.textTitleHistory.visibility = View.VISIBLE
        lifecycleScope.launchWhenStarted {
            val diagnosisRecyclerOptions = viewModel.getFirestoreRecyclerOptions()
            diagnosisFirestoreAdapter = diagnosisRecyclerOptions?.let { options ->
                DiagnosisFirestoreAdapter(
                    options,
                    Dispatchers.Default,
                    // User Event: Click DiagnosisRecycler ItemHolder
                    onItemClicked = { startActivity(interactivity(AppData.DISEASE, it)) },
                    // UI Event: Inform: No Diagnosis History
                    onPopulateList = { empty ->
                        layout.textNoDiagnosis.visibility = if (empty) View.VISIBLE else View.GONE
                    }
                )
            }
            diagnosisFirestoreAdapter?.startListening()

            layout.recyclerDiagnosis.also {
                it.visibility = View.VISIBLE
                it.setHasFixedSize(false)
                val orientation = LinearLayoutManager.HORIZONTAL
                it.layoutManager = LinearLayoutManager(
                    this@MainActivity,
                    orientation,
                    false
                )
                val divider = DividerItemDecoration(this@MainActivity, orientation)
                getDrawable(R.drawable.divider_horizontal)?.let { drawable ->
                    divider.setDrawable(drawable)
                }
                it.addItemDecoration(divider)
                it.adapter = diagnosisFirestoreAdapter
            }
        }

    }

    // UI Event: Memory Leak
    override fun onDestroy() {
        super.onDestroy()
        diagnosisFirestoreAdapter?.stopListening()
    }

    // User Event: Click Logout Button
    // NOTE: Shown via LogoutDialog, EmailVerificationDialog
    private fun logout() {
        val auth = SuggestionsProvider.AUTHORITY
        val mode = SuggestionsProvider.MODE
        val provider = SearchRecentSuggestions(this, auth, mode)
        viewModel.signOut(provider)

        Toast.makeText(this, getString(R.string.ui_text_warn_logout), Toast.LENGTH_SHORT).show()
        finish(); startActivity(interactivity())
    }

    // User Event: Click MenuItem
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // User Event: Click Account MenuItem
        R.id.action_account -> {
            if (viewModel.currentUser != null) {
                // UI Event: Dialog: if User exists
                val dialog = LogoutDialog(onPositiveButtonClick = { logout() })
                dialog.show(supportFragmentManager, LogoutDialog.TAG)
            } else {
                // UI Event: AccountActivity, if User NOT exists
                startActivity(Intent(this, AccountActivity::class.java))
            }
            true
        }
        R.id.action_settings -> {
            // User Event: Click Settings MenuItem
            startActivity(Intent(this, SettingsActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // UI Event: Menu
        menuInflater.inflate(R.menu.menu_main, menu)

        // UI Event: Search functionality
        val searchItem = menu.findItem(R.id.action_search)
        setupSearchView(searchItem, searchItem.actionView as SearchView)

        // UI Event: Search UX
        searchItem.setOnActionExpandListener(OnSearchExpandListener(menu))

        return super.onCreateOptionsMenu(menu)
    }

    private fun setupSearchView(item: MenuItem, view: SearchView) = lifecycleScope.launchWhenStarted {
        val searchManager = async { getSystemService(SEARCH_SERVICE) as SearchManager }
        val componentName = async { ComponentName(this@MainActivity, SearchableActivity::class.java) }
        view.setSearchableInfo(searchManager.await().getSearchableInfo(componentName.await()))

        view.findViewById<EditText>(R.id.search_src_text)?.also {
            // necessary: default MaterialTheme.SearchView(Day) is ugly
            val currentTheme = resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
            if (currentTheme != Configuration.UI_MODE_NIGHT_YES) {
                val hintColor = async {
                    ContextCompat.getColor(this@MainActivity, R.color.material_on_primary_disabled)
                }
                val textColor = async {
                    ContextCompat.getColor(this@MainActivity, R.color.white)
                }
                it.setHintTextColor(hintColor.await())
                it.setTextColor(textColor.await())
            }
            // close searchView: on soft keyboard down
            it.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) item.collapseActionView()
            }
        }
    }

    // region // launcher: Camera
    private val cameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isPermitted ->
            if (isPermitted) layout.buttonCamera.callOnClick()
        }


    private val cameraActivity =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap == null) return@registerForActivityResult
            runInference(UserInput.Bitmap(bitmap))
        }

    // endregion

    // region // launcher: Gallery
    private val galleryPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isPermitted ->
            if (isPermitted) layout.buttonGallery.callOnClick()
        }


    private val galleryActivity =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) return@registerForActivityResult
            runInference(UserInput.Uri(uri))
        }

    // endregion

    // region // ml: Any(Bitmap or Uri) -> viewModel -> DiseaseProfileActivity
    private fun runInference(userInput: UserInput) {
        layout.updateOnInference(View.VISIBLE)
        lifecycleScope.launchWhenStarted {
            val diagnosis = viewModel.startDiagnosisAsync(this@MainActivity, userInput).await()
            onInferenceResult(diagnosis)
            val d = viewModel.ran(this@MainActivity, userInput)
            onPytorchResult(d)
        }
    }

    private fun onPytorchResult(d: String?) {
        layout.textWelcome.text = d.toString()
    }

    private fun onInferenceResult(diseaseId: String) {
        layout.updateOnInference(View.INVISIBLE)
        Log.i(TAG, "onInferenceResult=$diseaseId")
        if (diseaseId == "null") {
            val dialog = DiagnosisFailDialog(onPositiveButtonClick = {
                // User Event: Click LearnMore Button
                startActivity(Intent(this, LearnMoreActivity::class.java))
            })
            dialog.show(supportFragmentManager, DiagnosisFailDialog.TAG)
            return
        }
        if (viewModel.currentUser != null) viewModel.saveDiagnosis(diseaseId)
        startActivity(interactivity(AppData.DISEASE, DiseaseText(diseaseId)))
    }

    private fun ActivityMainBinding.updateOnInference(visibility: Int) = when (visibility) {
        View.GONE, View.INVISIBLE -> {
            maskMain.visibility = View.GONE
            loadingInference.visibility = View.INVISIBLE
            divInference.visibility = View.GONE
        }
        else -> {
            maskMain.visibility = View.VISIBLE
            loadingInference.visibility = View.VISIBLE
            textAnalyzing.visibility = View.VISIBLE
            divInference.visibility = View.VISIBLE
        }
    }
    // endregion

    inner class OnSearchExpandListener(private val menu: Menu): MenuItem.OnActionExpandListener {
        override fun onMenuItemActionExpand(item: MenuItem?): Boolean = layout.run {
            maskMain.visibility = View.VISIBLE
            maskMain.setOnClickListener { item?.collapseActionView() }
            menu.findItem(R.id.action_settings)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
            menu.findItem(R.id.action_account)?.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
            return true
        }
        override fun onMenuItemActionCollapse(item: MenuItem?): Boolean = layout.run {
            maskMain.visibility = View.GONE
            this@MainActivity.invalidateOptionsMenu()
            return true
        }
    }

}
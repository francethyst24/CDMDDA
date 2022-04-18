package com.example.cdmdda.presentation

import android.Manifest
import android.app.SearchManager
import android.content.ComponentName
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.R
import com.example.cdmdda.common.Constants.CROP
import com.example.cdmdda.common.Constants.DISEASE
import com.example.cdmdda.common.Constants.HEALTHY
import com.example.cdmdda.common.Constants.INIT_QUERIES
import com.example.cdmdda.common.Constants.INPUT_IMAGE
import com.example.cdmdda.common.Constants.NULL
import com.example.cdmdda.common.Constants.ORIENTATION_X
import com.example.cdmdda.common.Constants.ORIENTATION_Y
import com.example.cdmdda.common.Constants.PERMISSION_GRANTED
import com.example.cdmdda.common.Constants.SHOW_AS_ACTION_NEVER
import com.example.cdmdda.common.Constants.UI_MODE_NIGHT_MASK
import com.example.cdmdda.common.Constants.UI_MODE_NIGHT_YES
import com.example.cdmdda.common.ContextUtils.intent
import com.example.cdmdda.common.ContextUtils.intentWith
import com.example.cdmdda.common.ContextUtils.toast
import com.example.cdmdda.data.dto.DiseaseText
import com.example.cdmdda.data.dto.UserInput
import com.example.cdmdda.databinding.ActivityMainBinding
import com.example.cdmdda.domain.usecase.*
import com.example.cdmdda.presentation.adapter.CropItemAdapter
import com.example.cdmdda.presentation.adapter.DiagnosisFirestoreAdapter
import com.example.cdmdda.presentation.fragment.DiagnosisFailDialog
import com.example.cdmdda.presentation.fragment.EmailVerificationDialog
import com.example.cdmdda.presentation.fragment.LogoutDialog
import com.example.cdmdda.data.repository.CropDataRepository
import com.example.cdmdda.data.repository.DataRepository
import com.example.cdmdda.presentation.viewmodel.MainViewModel
import com.example.cdmdda.presentation.viewmodel.factory.CreateWithFactory
import kotlinx.coroutines.async

class MainActivity : BaseCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private val cropResources: CropDataRepository by lazy { CropDataRepository(this) }
    private val dataRepository: DataRepository by lazy { DataRepository(this) }

    // region // declare: ViewBinding, ViewModel
    private val layout: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val viewModel: MainViewModel by viewModels {
        CreateWithFactory {
            MainViewModel(
                intent.getBooleanExtra(INIT_QUERIES, false),
                GetDiseaseDiagnosisUseCase(
                    PrepareBitmapUseCase(),
                    GetTfliteMLUseCase(),
                    GetPytorchMLUseCase(),
                    dataRepository
                ),
                GetDiagnosisHistoryUseCase(),
                GetCropItemUseCase(cropResources),
            )
        }
    }
    // endregion

    private var diagnosisFirestoreAdapter: DiagnosisFirestoreAdapter? = null
    private val cropItemAdapter: CropItemAdapter by lazy {
        CropItemAdapter(viewModel.cropList) {
            // User Event: Click CropRecycler ItemHolder
            startActivity(intentWith(extra = CROP, it))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(layout.toolbarMain)

//bell was here

        // User Event : Click Camera Button
        layout.buttonCamera.setOnClickListener {
            val requirement = Manifest.permission.CAMERA
            val isPermitted = ContextCompat.checkSelfPermission(this, requirement)
            if (isPermitted != PERMISSION_GRANTED) cameraPermission.launch(requirement)
            else cameraActivity.launch(null)
        }

        // User Event : Click Gallery Button
        layout.buttonGallery.setOnClickListener {
            val requirement = Manifest.permission.READ_EXTERNAL_STORAGE
            val isPermitted = ContextCompat.checkSelfPermission(this, requirement)
            if (isPermitted != PERMISSION_GRANTED) galleryPermission.launch(requirement)
            else galleryActivity.launch(INPUT_IMAGE)
        }

        // User Event : Click Cancel Button
        // NOTE: Shown via "Business Logic: Inference"
        layout.buttonCancelInference.setOnClickListener {
            //viewModel.cancelInference()
            viewModel.cancelDiagnosisAsync()
            toggleLoadingUI()
        }

        // UI Event: RecyclerView
        setCropRecyclerView()

        // UI Event: RecyclerView: Update data
        viewModel.cropCount(this).observe(this) {
            cropItemAdapter.notifyItemInserted(it)
        }

        // UI Event: TextView: Display, if User exists
        viewModel.apply {
            val displayName = currentUser?.email ?: getString(uiTextGuest)
            layout.textWelcome.text = getString(uiTextMain).plus(" $displayName")
        }

        // Multithreading: FirebaseUser
        lifecycleScope.launchWhenStarted {
            val isUserAuthenticated = viewModel.isUserAuthenticated(this@MainActivity)
            if (isUserAuthenticated) {
                // UI Event: RecyclerView, if User exists
                setDiagnosisRecyclerView()
                // UI Event : Dialog: if User NOT Verified
                //setEmailVerificationDialog()
                // TODO: Uncomment EmailVerification API
            }
        }

        setContentView(layout.root)
    }

    // UI Event: EmailVerificationDialog
    private fun setEmailVerificationDialog() {
        viewModel.currentUser?.let {
            it.reload().addOnCompleteListener { _ ->
                if (!it.isEmailVerified) {
                    val dialog = EmailVerificationDialog(it, onPositiveButtonClick = { logout() })
                    dialog.show(supportFragmentManager, EmailVerificationDialog.TAG)
                }
            }
        }
    }

    // UI Event: RecyclerView - Crops
    private fun setCropRecyclerView() = layout.recyclerCrops.run {
        setHasFixedSize(true)
        val divider = DividerItemDecoration(this@MainActivity, ORIENTATION_Y)
        getDrawable(viewModel.uiDrawDividerY)?.let { divider.setDrawable(it) }
        addItemDecoration(divider)
        layoutManager = LinearLayoutManager(this@MainActivity)
        adapter = cropItemAdapter
    }

    // UI Event: RecyclerView - Diagnosis
    private suspend fun setDiagnosisRecyclerView() = layout.run {
        textTitleHistory.visibility = View.VISIBLE
        val diagnosisRecyclerOptions = viewModel.getFirestoreRecyclerOptions()
        diagnosisFirestoreAdapter = diagnosisRecyclerOptions?.let { options ->
            DiagnosisFirestoreAdapter(
                this@MainActivity, options,
                // User Event: Click DiagnosisRecycler ItemHolder
                onItemClicked = { startActivity(intentWith(DISEASE, it)) },
                // UI Event: Inform: No Diagnosis History
                onPopulateList = { textNoDiagnosis.visibility = if (it) View.VISIBLE else View.GONE },
                // UI Event: Update: Recycler onChildAdded
                onChildAdded = { recyclerDiagnosis.scrollToPosition(0) },
            )
        }
        diagnosisFirestoreAdapter?.startListening()
        recyclerDiagnosis.run {
            visibility = View.VISIBLE
            setHasFixedSize(false)
            val orientation = ORIENTATION_X
            layoutManager = LinearLayoutManager(this@MainActivity, orientation, false)

            val divider = DividerItemDecoration(this@MainActivity, orientation)
            getDrawable(viewModel.uiDrawDividerX)?.let { divider.setDrawable(it) }
            addItemDecoration(divider)

            adapter = diagnosisFirestoreAdapter
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
        viewModel.signOut(this)
        toast(getString(viewModel.uiWarnLogout))
        finish(); startActivity(intentWith(/*recreate*/))
    }

    // User Event: Click MenuItem
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // User Event: Click Account MenuItem
        R.id.action_account -> {
            if (viewModel.isLoggedIn) {
                // UI Event: Dialog: if User exists
                val dialog = LogoutDialog(onPositiveButtonClick = { logout() })
                dialog.show(supportFragmentManager, LogoutDialog.TAG)
            } else {
                // UI Event: AccountActivity, if User NOT exists
                startActivity(intent(AccountActivity::class.java))
            }
            true
        }
        R.id.action_settings -> {
            // User Event: Click Settings MenuItem
            startActivity(intent(SettingsActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // UI Event: Menu
        menuInflater.inflate(viewModel.uiMenuMain, menu)
        // UI Event: Search functionality
        val searchItem = menu.findItem(viewModel.uiMenuItemSearch)
        setupSearchView(searchItem, searchItem.actionView as SearchView)
        // UI Event: Search UX
        searchItem.setOnActionExpandListener(OnSearchExpandListener(menu))
        return super.onCreateOptionsMenu(menu)
    }

    // region // launcher: Camera
    private val cameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isPermitted ->
            if (isPermitted) layout.buttonCamera.callOnClick()
        }


    private val cameraActivity =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            if (it == null) return@registerForActivityResult
            startDiagnosis(UserInput.Bmp(it))
        }

    // endregion

    // region // launcher: Gallery
    private val galleryPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isPermitted ->
            if (isPermitted) layout.buttonGallery.callOnClick()
        }


    private val galleryActivity =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it == null) return@registerForActivityResult
            startDiagnosis(UserInput.Uri(it))
        }

    // endregion

    // region // ml: Any(Bmp or Uri) -> viewModel -> DiseaseProfileActivity
    private fun startDiagnosis(userInput: UserInput) {
        toggleLoadingUI()
        lifecycleScope.launchWhenStarted {
            val diagnosis = viewModel.startDiagnosisAsync(this@MainActivity, userInput).await()
            onDiagnosisResult(diagnosis)
        }
    }

    private fun onPytorchResult(d: String) {
        layout.textWelcome.text = d
    }

    private fun onDiagnosisResult(diseaseId: String) {
        toggleLoadingUI()
        when (diseaseId) {
            NULL, HEALTHY -> {
                val dialog = DiagnosisFailDialog(
                    hasLeafDetected = diseaseId != NULL,
                    // User Event: Click LearnMore Button
                    onPositiveButtonClick = { startActivity(intent(LearnMoreActivity::class.java)) }
                )
                dialog.show(supportFragmentManager, DiagnosisFailDialog.TAG)
                return
            }
        }
        if (viewModel.isLoggedIn) viewModel.saveDiagnosis(diseaseId)
        startActivity(intentWith(extra = DISEASE, DiseaseText(diseaseId)))
    }

    private fun toggleLoadingUI() = layout.run {
        if (maskMain.visibility == View.VISIBLE) {
            maskMain.visibility = View.GONE
            loadingInference.visibility = View.INVISIBLE
            divInference.visibility = View.GONE
        } else {
            maskMain.visibility = View.VISIBLE
            loadingInference.visibility = View.VISIBLE
            textAnalyzing.visibility = View.VISIBLE
            divInference.visibility = View.VISIBLE
        }
    }
    // endregion

    private fun setupSearchView(item: MenuItem, view: SearchView) = lifecycleScope.launchWhenStarted {
        val searchManager = async { getSystemService(SEARCH_SERVICE) as SearchManager }
        val componentName = async { ComponentName(this@MainActivity, SearchableActivity::class.java) }
        view.setSearchableInfo(searchManager.await().getSearchableInfo(componentName.await()))

        view.findViewById<EditText>(R.id.search_src_text)?.also {
            // necessary: default MaterialTheme.SearchView(Day) is ugly
            val currentTheme = resources?.configuration?.uiMode?.and(UI_MODE_NIGHT_MASK)
            if (currentTheme != UI_MODE_NIGHT_YES) {
                val hintColor = async {
                    ContextCompat.getColor(this@MainActivity, viewModel.uiColrDisabledOnPrimary)
                }
                val textColor = async {
                    ContextCompat.getColor(this@MainActivity, viewModel.uiColrWhite)
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

    inner class OnSearchExpandListener(private val menu: Menu) : MenuItem.OnActionExpandListener {
        override fun onMenuItemActionExpand(item: MenuItem?): Boolean = layout.run {
            maskMain.visibility = View.VISIBLE
            maskMain.setOnClickListener { item?.collapseActionView() }
            viewModel.uiMenuItemOthers.forEach { menu.findItem(it)?.setShowAsAction(SHOW_AS_ACTION_NEVER) }
            true
        }

        override fun onMenuItemActionCollapse(item: MenuItem?): Boolean = layout.run {
            maskMain.visibility = View.GONE
            this@MainActivity.invalidateOptionsMenu()
            true
        }
    }

}
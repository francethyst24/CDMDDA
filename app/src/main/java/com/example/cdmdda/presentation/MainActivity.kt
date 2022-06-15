package com.example.cdmdda.presentation

import android.Manifest
import android.app.SearchManager
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.DiagnosisHistoryActivity
import com.example.cdmdda.R
import com.example.cdmdda.common.Constants.CROP
import com.example.cdmdda.common.Constants.DISEASE
import com.example.cdmdda.common.Constants.HEALTHY
import com.example.cdmdda.common.Constants.INIT_QUERIES
import com.example.cdmdda.common.Constants.LABELS
import com.example.cdmdda.common.Constants.NULL
import com.example.cdmdda.common.DiagnosisRecyclerOptions
import com.example.cdmdda.common.utils.AndroidUtils.HINT_COLOR
import com.example.cdmdda.common.utils.AndroidUtils.INPUT_IMAGE
import com.example.cdmdda.common.utils.AndroidUtils.ORIENTATION_LANDSCAPE
import com.example.cdmdda.common.utils.AndroidUtils.ORIENTATION_X
import com.example.cdmdda.common.utils.AndroidUtils.ORIENTATION_Y
import com.example.cdmdda.common.utils.AndroidUtils.PERMISSION_GRANTED
import com.example.cdmdda.common.utils.AndroidUtils.SHOW_AS_ACTION_NEVER
import com.example.cdmdda.common.utils.AndroidUtils.SHOW_DIAGNOSIS_REQUEST
import com.example.cdmdda.common.utils.AndroidUtils.SHOW_DIAGNOSIS_RESULT
import com.example.cdmdda.common.utils.AndroidUtils.TEXT_COLOR
import com.example.cdmdda.common.utils.AndroidUtils.UI_MODE_NIGHT_MASK
import com.example.cdmdda.common.utils.AndroidUtils.UI_MODE_NIGHT_YES
import com.example.cdmdda.common.utils.AndroidUtils.checkSelfPermissionCompat
import com.example.cdmdda.common.utils.AndroidUtils.getColorCompat
import com.example.cdmdda.common.utils.AndroidUtils.getComponentName
import com.example.cdmdda.common.utils.AndroidUtils.getStringArray
import com.example.cdmdda.common.utils.AndroidUtils.intent
import com.example.cdmdda.common.utils.AndroidUtils.intentWith
import com.example.cdmdda.common.utils.AndroidUtils.observeOnce
import com.example.cdmdda.common.utils.AndroidUtils.restartMain
import com.example.cdmdda.common.utils.AndroidUtils.setResultListener
import com.example.cdmdda.common.utils.AndroidUtils.toast
import com.example.cdmdda.data.dto.DiseaseText
import com.example.cdmdda.databinding.ActivityMainBinding
import com.example.cdmdda.domain.model.Diagnosable
import com.example.cdmdda.domain.usecase.GetDiagnosisHistoryUseCase
import com.example.cdmdda.domain.usecase.GetDiseaseDiagnosisUseCase
import com.example.cdmdda.domain.usecase.GetPytorchMLUseCase
import com.example.cdmdda.domain.usecase.PrepareBitmapUseCase
import com.example.cdmdda.presentation.adapter.CropItemAdapter
import com.example.cdmdda.presentation.adapter.DiagnosisAdapter
import com.example.cdmdda.presentation.fragment.LogoutDialog
import com.example.cdmdda.presentation.fragment.LogoutDialog.OnLogoutListener
import com.example.cdmdda.presentation.fragment.ShowDiagnosisDialog
import com.example.cdmdda.presentation.fragment.StartDiagnosisDialog
import com.example.cdmdda.presentation.fragment.StartDiagnosisDialog.OnDiagnosisResultListener
import com.example.cdmdda.presentation.fragment.VerifyEmailDialog
import com.example.cdmdda.presentation.viewmodel.MainViewModel
import com.example.cdmdda.presentation.viewmodel.factory.viewModelBuilder
import kotlinx.coroutines.async

class MainActivity : BaseCompatActivity(), OnLogoutListener, OnDiagnosisResultListener {
    companion object {
        const val TAG = "MainActivity"
    }

    // region // declare: ViewBinding, ViewModel
    private val layout by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val model by viewModelBuilder {
        MainViewModel(
            intent.getBooleanExtra(INIT_QUERIES, false),
            GetDiagnosisHistoryUseCase(),
            GetDiseaseDiagnosisUseCase(
                PrepareBitmapUseCase(),
                GetPytorchMLUseCase(),
                getStringArray(LABELS),
            ),
        )
    }
    // endregion

    private var diagnosisAdapter: DiagnosisAdapter? = null
    private val cropItemAdapter by lazy {
        CropItemAdapter(model.cropList) {
            // User Event: Click CropRecycler ItemHolder
            startActivity(intentWith(extra = CROP, it))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(layout.toolbarMain)

        supportFragmentManager.setResultListener(this, SHOW_DIAGNOSIS_REQUEST) { bundle ->
            val positiveBtnClicked = bundle.getBoolean(SHOW_DIAGNOSIS_RESULT, false)
            if (positiveBtnClicked) startActivity(intent(LearnMoreActivity::class.java))
        }

//bell was here

        // User Event : Click Camera Button
        layout.buttonCamera.setOnClickListener {
            val requirement = Manifest.permission.CAMERA
            val isPermitted = checkSelfPermissionCompat(requirement)
            if (isPermitted != PERMISSION_GRANTED) cameraPermission.launch(requirement)
            else cameraActivity.launch(null)
        }

        // User Event : Click Gallery Button
        layout.buttonGallery.setOnClickListener {
            val requirement = Manifest.permission.READ_EXTERNAL_STORAGE
            val isPermitted = checkSelfPermissionCompat(requirement)
            if (isPermitted != PERMISSION_GRANTED) galleryPermission.launch(requirement)
            else galleryActivity.launch(INPUT_IMAGE)
        }

        // User Event : Click Cancel Button
        // NOTE: Shown via "Business Logic: Inference"
        layout.buttonCancelInference.setOnClickListener {
            //viewModel.cancelInference()
            model.cancelDiagnosis()
            toggleLoadingUI()
        }

        layout.textShowAll.setOnClickListener {
            val intentTo = intent(DiagnosisHistoryActivity::class.java)
            startActivity(intentTo)
        }
        // UI Event: RecyclerView
        setCropRecyclerView()

        // UI Event: RecyclerView: Update data
        model.cropCount(this).observe(this) {
            cropItemAdapter.notifyItemInserted(it)
        }

        // UI Event: TextView: Display, if User exists
        with(model) {
            val userEmail = currentUser?.email ?: getString(uiTextGuest)
            layout.textWelcome.text = buildString {
                append(getString(uiTextMain))
                append(" ")
                append(userEmail.substringBefore("@"))
            }
        }

        // UI Event: LoadingUI persist configChange
        /*viewModel.diagnosableInput?.startDiagnosis()*/
        model.userDiagnosableState.observe(this) { it?.startDiagnosis() }

        // Multithreading: FirebaseUser
        if (model.isLoggedIn) {
            observeDiagnosisUiState()
            // UI Event : Dialog: if User NOT Verified
            model.verifyEmailDialogUiState.observe(this) {
                if (it) {
                    setEmailVerificationDialog()
                    model.finishedShowingVerifyEmailDialog()
                }
            }

            lifecycleScope.launchWhenStarted {
                with(this@MainActivity) {
                    model.initSearchQueries(this)
                    // UI Event: RecyclerView, if User exists
                    model.getDiagnosisOptions()?.let { setDiagnosisRecyclerView(it) }
                }
            }
        }
        setContentView(layout.root)
    }

    private fun observeDiagnosisUiState() {
        model.loadingDiagnosisUiState.observe(this) {
            if (!it) layout.loadingDiagnosis.visibility = View.GONE
        }
        model.isEmptyDiagnosisUiState.observe(this) {
            layout.textNoDiagnosis.visibility = if (it) {
                diagnosisAdapter?.notifyDataSetChanged()
                View.VISIBLE
            } else View.GONE
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) = with(layout) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == ORIENTATION_LANDSCAPE) {
            val diagnosisGroup = if (model.isLoggedIn) {
                listOf(recyclerDiagnosis, textNoDiagnosis, loadingDiagnosis, textShowAll)
            } else emptyList()
            val viewGroup = listOf(textWelcome, textTitleHistory)
            diagnosisGroup.plus(viewGroup).forEach {
                it.visibility = View.GONE
            }
            toast(R.string.ui_warn_landscape)
            textInstructions.isSingleLine = true
        } else {
            val diagnosisGroup = if (model.isLoggedIn) {
                observeDiagnosisUiState()
                listOf(recyclerDiagnosis, textTitleHistory, textShowAll)
            } else emptyList()
            diagnosisGroup.plus(textWelcome).forEach {
                it.visibility = View.VISIBLE
            }
            textInstructions.isSingleLine = false
        }
    }

    // UI Event: VerifyEmailDialog
    private fun setEmailVerificationDialog() {
        model.reloadUser()
        model.isEmailVerified.observeOnce(this) {
            if (!it) VerifyEmailDialog().show(supportFragmentManager, VerifyEmailDialog.TAG)
        }
    }

    // UI Event: RecyclerView - Crops
    private fun setCropRecyclerView() = with(layout.recyclerCrops) {
        setHasFixedSize(true)
        val divider = DividerItemDecoration(this@MainActivity, ORIENTATION_Y)
        getDrawable(model.uiDrawDividerY)?.let { divider.setDrawable(it) }
        addItemDecoration(divider)
        layoutManager = LinearLayoutManager(this@MainActivity)
        adapter = cropItemAdapter
    }

    // UI Event: RecyclerView - Diagnosis
    private fun setDiagnosisRecyclerView(options: DiagnosisRecyclerOptions) = with(layout) {
        listOf(loadingDiagnosis, textTitleHistory, textShowAll).forEach {
            it.visibility = View.VISIBLE
        }
        diagnosisAdapter = DiagnosisAdapter(
            lifecycleOwner = this@MainActivity,
            options = options,
            orientation = ORIENTATION_X,
            // User Event: Click DiagnosisRecycler ItemHolder
            onItemClicked = { startActivity(intentWith(DISEASE, it)) },
            // UI Event: Inform: No Diagnosis History
            onPopulateList = { isEmpty ->
                model.finishedLoadingDiagnosis()
                model.finishedReturnedDiagnosis(isEmpty)
            },
            // UI Event: Update: scrollToStart onChildAdded
            onChildAdded = {
                if (recyclerDiagnosis.layoutManager != null) {
                    recyclerDiagnosis.scrollToPosition(0)
                }
            },
        )
        diagnosisAdapter?.startListening()
        with(recyclerDiagnosis) {
            visibility = View.VISIBLE
            setHasFixedSize(false)
            with(this@MainActivity) {
                layoutManager = LinearLayoutManager(this, ORIENTATION_X, false)
                val divider = DividerItemDecoration(this, ORIENTATION_X)
                getDrawable(model.uiDrawDividerX)?.let { divider.setDrawable(it) }
                addItemDecoration(divider)
            }
            adapter = diagnosisAdapter
        }
    }


    // UI Event: Memory Leak
    override fun onDestroy() {
        diagnosisAdapter?.stopListening()
        super.onDestroy()
    }

    // User Event: Click Logout Button
    // NOTE: Shown via LogoutDialog, VerifyEmailDialog
    override fun logout() {
        model.signOut(this)
        toast(model.uiWarnLogout)
        restartMain()
    }

    // User Event: Click MenuItem
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // User Event: Click Account MenuItem
        model.uiMenuItemAccount -> {
            if (model.isLoggedIn) {
                // UI Event: if User exists
                LogoutDialog().show(supportFragmentManager, LogoutDialog.TAG)
            } else {
                // UI Event: if User NOT exists
                startActivity(intent(AccountActivity::class.java))
            }
            true
        }
        model.uiMenuItemSettings -> {
            // User Event: Click Settings MenuItem
            startActivity(intent(SettingsActivity::class.java))
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    // region // init: Search functionality
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // UI Event: Menu
        menuInflater.inflate(model.uiMenuMain, menu)
        // UI Event: Search functionality
        val searchItem = menu.findItem(model.uiMenuItemSearch)
        searchItem.setupSearchView()
        // UI Event: Search UX
        searchItem.setOnActionExpandListener(OnSearchExpandListener(menu))
        return super.onCreateOptionsMenu(menu)
    }

    private fun MenuItem.setupSearchView() = lifecycleScope.launchWhenCreated {
        val view = actionView as SearchView
        view.maxWidth = Int.MAX_VALUE
        val attributes = with(this@MainActivity) {
            val searchManager = async { getSystemService(SEARCH_SERVICE) as SearchManager }
            val componentName = async { getComponentName(SearchableActivity::class.java) }
            view.setSearchableInfo(searchManager.await().getSearchableInfo(componentName.await()))

            val hintColor = async { getColorCompat(model.uiColrDisabledOnPrimary) }
            val textColor = async { getColorCompat(model.uiColrWhite) }
            return@with mapOf(
                HINT_COLOR to hintColor.await(),
                TEXT_COLOR to textColor.await(),
            )
        }

        view.findViewById<EditText>(model.uiMenuSearchEditText)?.let {
            // necessary: default MaterialTheme.SearchView(Day) is ugly
            it.adjustTheme(attributes)
            // close searchView: on soft keyboard down
            it.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) collapseActionView() }
        }
    }

    private fun EditText.adjustTheme(styles: Map<String, Int>) {
        val currentTheme = resources?.configuration?.uiMode?.and(UI_MODE_NIGHT_MASK)
        if (currentTheme != UI_MODE_NIGHT_YES) {
            setHintTextColor(styles.getValue(HINT_COLOR))
            setTextColor(styles.getValue(TEXT_COLOR))
        }
    }

    inner class OnSearchExpandListener(private val menu: Menu) : MenuItem.OnActionExpandListener {
        override fun onMenuItemActionExpand(item: MenuItem?): Boolean = with(layout) {
            maskMain.visibility = View.VISIBLE
            maskMain.setOnClickListener { item?.collapseActionView() }
            model.uiMenuItemOthers.forEach { menu.findItem(it)?.setShowAsAction(SHOW_AS_ACTION_NEVER) }
            true
        }

        override fun onMenuItemActionCollapse(item: MenuItem?): Boolean = with(layout) {
            maskMain.visibility = View.GONE
            this@MainActivity.invalidateOptionsMenu()
            true
        }
    }
    // endregion

    // region // launcher: Camera
    private val cameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { if (it) layout.buttonCamera.callOnClick() }


    private val cameraActivity = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) {
        if (it != null) model.submitDiagnosable(Diagnosable.Bmp(it))
    }

    // endregion

    // region // launcher: Gallery
    private val galleryPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { if (it) layout.buttonGallery.callOnClick() }


    private val galleryActivity = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        if (it != null) model.submitDiagnosable(Diagnosable.Uri(it))
    }

    // endregion

    // region // ml: Any(Bmp or Uri) -> viewModel -> DiseaseProfileActivity
    private fun Diagnosable.startDiagnosis() {
        //toggleLoadingUI()
        StartDiagnosisDialog().show(supportFragmentManager, StartDiagnosisDialog.TAG)
        val context = this@MainActivity
        model.launchDiagnosis(context, this).observeOnce(context) {
            /*model.finishDiagnosableSubmission()
            model.clearDiagnosisResult()
            toggleLoadingUI()
            onDiagnosisResult(it)*/
        }
    }

    private fun onDiagnosisResult(diseaseId: String) = when (diseaseId) {
        NULL, HEALTHY -> {
            val dialog = ShowDiagnosisDialog.newInstance(diseaseId != NULL)
            dialog.show(supportFragmentManager, ShowDiagnosisDialog.TAG)
        }
        // UI Event: Diagnosis yielded result
        else -> startActivity(intentWith(DISEASE, DiseaseText(diseaseId)))
    }

    private fun toggleLoadingUI() = with(layout) {
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

    override fun onGotoDiseaseClick(diseaseId: String) {
        val intentWithId = intentWith(DISEASE, DiseaseText(diseaseId))
        startActivity(intentWithId)
    }

    // endregion

}
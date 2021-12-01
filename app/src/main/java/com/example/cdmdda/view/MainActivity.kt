package com.example.cdmdda.view

import android.Manifest
import android.app.Activity
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder.*
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.R
import com.example.cdmdda.view.adapter.CropFirestoreAdapter
import com.example.cdmdda.view.adapter.DiagnosisFirestoreAdapter
import com.example.cdmdda.databinding.ActivityMainBinding
import com.example.cdmdda.view.fragment.DiagnosisFailureDialog
import com.example.cdmdda.view.fragment.LogoutDialog
import com.example.cdmdda.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

object DisplayUtils {

    fun generateLinks(textView: TextView, padding : Int = -1, vararg links: Pair<String, View.OnClickListener>) {
        val spannableString = SpannableString(textView.text)
        var start = padding // 9

        for (link in links) {
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

            start = textView.text.toString().indexOf(link.first, start + 1)
            if (start == -1) continue
            spannableString.setSpan(
                clickableSpan, start, start + link.first.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    fun attachListeners(context: Context, list: List<String>) : List<Pair<String, View.OnClickListener>> {
        val mutableList = mutableListOf<Pair<String, View.OnClickListener>>()
        for (item in list) {
            mutableList.add(item to View.OnClickListener {
                var displayIntent = Intent()
                when (context) {
                    is DisplayDiseaseActivity -> {
                        displayIntent = Intent(context, DisplayCropActivity::class.java)
                        displayIntent.putExtra("crop_id", item)
                    }
                    is DisplayCropActivity -> {
                        displayIntent = Intent(context, DisplayDiseaseActivity::class.java)
                        displayIntent.putExtra("disease_id", item)
                    }
                }
                context.startActivity(displayIntent)
                (context as Activity).finish()
            })
        }
        return mutableList
    }

    fun formatDate(string: String, date: Date) : String = SimpleDateFormat(string, Locale.getDefault()).format(date)
}

class MainActivity : AppCompatActivity(), LogoutDialog.LogoutDialogListener, CropFirestoreAdapter.CropEventListener, DiagnosisFirestoreAdapter.DiagnosisEventListener {

    // region // declare: ViewBinding, ViewModel
    private lateinit var layout: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    // endregion

    // declare: Firebase(Auth)
    private lateinit var auth: FirebaseAuth

    // region // declare: FirestoreRecyclerAdapter
    private var cropFirestoreAdapter: CropFirestoreAdapter? = null
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
        layout.buttonCamera.setOnClickListener{
            val isCameraPermitted = ContextCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.CAMERA)
            if (isCameraPermitted != PackageManager.PERMISSION_GRANTED) {
                registerCameraPermission.launch(Manifest.permission.CAMERA)
            } else { registerCameraLauncher.launch(null) }
        }

        layout.buttonGallery.setOnClickListener{
            val isGalleryPermitted = ContextCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (isGalleryPermitted != PackageManager.PERMISSION_GRANTED) {
                registerGalleryPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else { registerGalleryLauncher.launch("image/*") }
        }

        layout.buttonCancelInference.setOnClickListener {
            viewModel.cancelInference()
            updateUIOnInference(View.INVISIBLE)
        }
        // endregion

        // init: Firebase - Auth
        auth = Firebase.auth

        // init: RecyclerView
        setMainRecyclerView()
        if (layout.textUserId.text != getString(R.string.text_guest)) setDiagnosisRecyclerView()
    }

    // region // init: RecyclerView
    private fun setMainRecyclerView() {
        cropFirestoreAdapter = CropFirestoreAdapter(viewModel.mainRecyclerOptions)
        layout.rcvCrops.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = cropFirestoreAdapter
        }
        cropFirestoreAdapter!!.setOnItemClickListener(this@MainActivity)
    }

    private fun setDiagnosisRecyclerView() {
        layout.rcvDiagnosis.visibility = View.VISIBLE
        diagnosisFirestoreAdapter = DiagnosisFirestoreAdapter(viewModel.diagnosisRecyclerOptions)
        layout.rcvDiagnosis.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = diagnosisFirestoreAdapter
        }
        diagnosisFirestoreAdapter!!.setOnItemClickListener(this@MainActivity)
    }
    // endregion

    // region // lifecycle
    override fun onStart() {
        super.onStart()
        // START data flow
        cropFirestoreAdapter?.startListening()

        // region // update: UI -> authStateChanged
        layout.textUserId.text = if (viewModel.getUserDiagnosisHistory()) {
            setDiagnosisRecyclerView()
            diagnosisFirestoreAdapter?.startListening()
            auth.currentUser?.email
        } else {
            this@MainActivity.getString(R.string.text_guest)
        }
        // endregion

    }

    override fun onStop() {
        super.onStop()
        // region // STOP data flow - avoid memory leaks
        cropFirestoreAdapter?.stopListening()
        diagnosisFirestoreAdapter?.stopListening()
        // endregion
    }
    // endregion

    // region // events: RecyclerView.Item
    override fun onCropItemClick(documentSnapshot: DocumentSnapshot, position: Int) {
        val displayCropIntent = Intent(this@MainActivity, DisplayCropActivity::class.java)
        displayCropIntent.putExtra("crop_id", documentSnapshot.id)
        startActivity(displayCropIntent)
    }

    override fun onCropQueryReturned(itemCount: Int) {
        if (itemCount != 0) layout.loadingCrops.hide()
        else layout.textNoCrops.visibility = View.VISIBLE
    }

    override fun onDiagnosisItemClick(documentSnapshot: DocumentSnapshot, position: Int) {
        val displayDiseaseIntent = Intent(this@MainActivity, DisplayDiseaseActivity::class.java)
        displayDiseaseIntent.putExtra("disease_id", documentSnapshot.getString("name"))
        startActivity(displayDiseaseIntent)
    }

    // endregion

    // region // events: LogoutDialog
    override fun onLogoutClick(fragment: AppCompatDialogFragment) {
        auth.signOut()
        finish()
        startActivity(this@MainActivity.intent)
        Toast.makeText(this@MainActivity, "Logged out", Toast.LENGTH_SHORT).show()
    }
    // endregion

    // events: MenuItem
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_search -> { true }
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
        else -> { super.onOptionsItemSelected(item) }
    }

    // inflate: menu -> actionBar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        // region // implement: Search
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val componentName = ComponentName(this@MainActivity, SearchableActivity::class.java)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        // endregion

        // region // fixes: search UI
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                layout.mainMask.visibility = View.VISIBLE
                // close searchView: on background click
                layout.mainMask.setOnClickListener { searchItem.collapseActionView() }
                (menu.findItem(R.id.action_settings)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                (menu.findItem(R.id.action_account)).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                layout.mainMask.visibility = View.GONE
                this@MainActivity.invalidateOptionsMenu()
                return true
            }

        })

        searchView.findViewById<EditText>(R.id.search_src_text).apply {
            // necessary: default MaterialTheme.SearchView is ugly
            setHintTextColor(ContextCompat.getColor(this@MainActivity, R.color.material_on_primary_disabled))
            setTextColor(ContextCompat.getColor(this@MainActivity, R.color.white))
            // close searchView: on soft keyboard down
            setOnFocusChangeListener { v, hasFocus ->
                if (!hasFocus) searchItem.collapseActionView()
            }
        }
        // endregion

        return super.onCreateOptionsMenu(menu)
    }

    // region // launcher: Camera
    private val registerCameraPermission = registerForActivityResult(RequestPermission()) {
        if (it) layout.buttonCamera.callOnClick()
    }

    private val registerCameraLauncher = registerForActivityResult(TakePicturePreview()) { bitmap ->
        if (bitmap == null) return@registerForActivityResult
        runInference(bitmap)
    }
    // endregion

    // region // launcher: Gallery
    private val registerGalleryPermission = registerForActivityResult(RequestPermission()) {
        if (it) layout.buttonGallery.callOnClick()
    }

    private val registerGalleryLauncher = registerForActivityResult(GetContent()) {
        if (it == null) return@registerForActivityResult
        val bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(contentResolver, it)
        } else {
            decodeBitmap(createSource(contentResolver, it)).copy(Bitmap.Config.ARGB_8888, true)
        }
        runInference(bitmap)
    }
    // endregion

    // region // ml: Bitmap -> viewModel -> DiseaseActivity
    private fun runInference(bitmap: Bitmap) {
        updateUIOnInference(View.VISIBLE)
        viewModel.runInference(bitmap).observe(this@MainActivity) {
            startDiseaseActivity(it)
        }
    }

    private fun startDiseaseActivity(diseaseId: String) {
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

    private fun updateUIOnInference(int: Int) {
        if (int == View.INVISIBLE) {
            layout.apply {
                mainMask.visibility = View.GONE
                loadingInference.visibility = View.INVISIBLE
                containerInference.visibility = View.GONE
            }
        }
        else {
            layout.apply {
                mainMask.visibility = View.VISIBLE
                loadingInference.visibility = View.VISIBLE
                textAnalyzing.visibility = View.VISIBLE
                containerInference.visibility = View.VISIBLE
            }
        }
    }
    // endregion

}

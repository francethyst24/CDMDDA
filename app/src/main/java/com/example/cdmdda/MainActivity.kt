package com.example.cdmdda


import android.Manifest
import android.app.Activity
import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.createScaledBitmap
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.adapters.CropAdapter
import com.example.cdmdda.adapters.DiagnosisAdapter
import com.example.cdmdda.databinding.ActivityMainBinding
import com.example.cdmdda.fragments.LogoutFragment
import com.example.cdmdda.ml.DiseaseDetector
import com.example.cdmdda.viewmodels.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
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

class MainActivity : AppCompatActivity(), LogoutFragment.LogoutFragmentListener, CropAdapter.OnItemClickListener, DiagnosisAdapter.OnItemClickListener {

    // region -- declare: ViewBinding, ViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    // endregion

    // region -- declare: Firebase - Auth, Firestore
    private lateinit var auth: FirebaseAuth
    // private val db: FirebaseFirestore = Firebase.firestore
    // private val cropRef : CollectionReference = db.collection("crops")
    // endregion

    // region -- declare: FirestoreRecyclerAdapter
    private var cropAdapter: CropAdapter? = null
    private var diagnosisAdapter: DiagnosisAdapter? = null

    // endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // region -- init: ViewBinding, ViewModel, Toolbar
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setSupportActionBar(binding.toolbarMain)
        setContentView(binding.root)
        // endregion

//bell was here

        // region -- events: button
        binding.buttonCamera.setOnClickListener{
            val isCameraPermitted = ContextCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.CAMERA)
            if (isCameraPermitted != PackageManager.PERMISSION_GRANTED) {
                registerCameraPermission.launch(Manifest.permission.CAMERA)
            } else { registerCameraLauncher.launch(null) }
        }

        binding.buttonGallery.setOnClickListener{
            val isGalleryPermitted = ContextCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
            if (isGalleryPermitted != PackageManager.PERMISSION_GRANTED) {
                registerGalleryPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else { registerGalleryLauncher.launch("image/*") }
        }
        // endregion

        // region -- init: Firebase - Auth
        auth = Firebase.auth
        // endregion

        setMainRecyclerView()
        if (binding.textUserId.text != getString(R.string.text_guest)) setDiagnosisRecyclerView()
    }

    // region -- init: RecyclerView
    private fun setMainRecyclerView() {
        cropAdapter = CropAdapter(viewModel.mainRecyclerOptions)
        binding.rcvCrops.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = cropAdapter
        }
        cropAdapter!!.setOnItemClickListener(this@MainActivity)
    }

    private fun setDiagnosisRecyclerView() {
        binding.rcvDiagnosis.visibility = View.VISIBLE
        diagnosisAdapter = DiagnosisAdapter(viewModel.diagnosisRecyclerOptions)
        binding.rcvDiagnosis.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = diagnosisAdapter
        }
        diagnosisAdapter!!.setOnItemClickListener(this@MainActivity)
    }
    // endregion

    // region -- events: RecyclerView.Item
    override fun onCropItemClick(documentSnapshot: DocumentSnapshot, position: Int) {
        val displayCropIntent = Intent(this@MainActivity, DisplayCropActivity::class.java)
        displayCropIntent.putExtra("crop_id", documentSnapshot.id)
        startActivity(displayCropIntent)
    }

    override fun onDiagnosisItemClick(documentSnapshot: DocumentSnapshot, position: Int) {
        val displayDiseaseIntent = Intent(this@MainActivity, DisplayDiseaseActivity::class.java)
        displayDiseaseIntent.putExtra("disease_id", documentSnapshot.id)
        startActivity(displayDiseaseIntent)
    }
    // endregion

    // events: menu
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_search -> { true }
        R.id.action_account -> {
            if (auth.currentUser == null) {
                startActivity(Intent(this@MainActivity, AccountActivity::class.java))
            } else {
                val dialog = LogoutFragment()
                dialog.show(supportFragmentManager, "LogoutFragment")
            }
            true
        }
        R.id.action_settings -> { true }
        else -> { super.onOptionsItemSelected(item) }
    }

    // inflate: menu -> actionBar
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val settingsItem = menu.findItem(R.id.action_settings)
        val accountItem = menu.findItem(R.id.action_account)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val componentName = ComponentName(this@MainActivity, SearchableFragment::class.java)
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                settingsItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                accountItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER)
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                this@MainActivity.invalidateOptionsMenu()
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    // region -- lifecycle
    override fun onStart() {
        super.onStart()
        // region -- START data flow
        cropAdapter?.startListening()
        // endregion

        // region -- update: UI -> authStateChanged
        binding.textUserId.text = if (viewModel.reload()) {
            setDiagnosisRecyclerView()
            diagnosisAdapter?.startListening()
            viewModel.user?.email
        } else {
            this@MainActivity.getString(R.string.text_guest)
        }
        // endregion

    }

    override fun onStop() {
        super.onStop()
        // region -- STOP data flow - avoid memory leaks
        cropAdapter?.stopListening()
        diagnosisAdapter?.stopListening()
        // endregion
    }
    // endregion

    // region -- events: logoutDialogFragment
    override fun onLogoutClick(fragment: AppCompatDialogFragment) {
        auth.signOut()
        finish()
        startActivity(this@MainActivity.intent)
        Toast.makeText(this@MainActivity, "Logged out", Toast.LENGTH_SHORT).show()
    }
    // endregion

    // region -- launcher: Camera
    private val registerCameraPermission = registerForActivityResult(RequestPermission()) {
        if (it) binding.buttonCamera.callOnClick()
    }

    private val registerCameraLauncher = registerForActivityResult(TakePicturePreview()) { bitmap ->
        if (bitmap == null) return@registerForActivityResult
        runInference(bitmap)
    }
    // endregion

    // region -- launcher: Gallery
    private val registerGalleryPermission = registerForActivityResult(RequestPermission()) {
        if (it) binding.buttonGallery.callOnClick()
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

    // ml: Bitmap -> Inference
    private fun runInference(bitmap: Bitmap) {
        binding.pbInferProgress.visibility = View.VISIBLE
        GlobalScope.launch(Dispatchers.Main) {
            val result = viewModel.runInference(bitmap)
            startDiseaseActivity(result.await())
        }
        // startDiseaseActivity(viewModel.runInference(bitmap))
        // return (0 until viewModel.diseaseList.size).random()
    }

    private fun startDiseaseActivity(string: String) {
        binding.pbInferProgress.visibility = View.GONE
        if (string == "null") {
            AlertDialog.Builder(this@MainActivity).apply {
                setTitle(R.string.dialog_title_undiagnosed)
                setMessage(R.string.dialog_message_undiagnosed)
                setPositiveButton(R.string.dialog_button_ok, null)
            }.create().show()
            return
        }

        var diseaseId : String
        viewModel.diseaseList.apply { diseaseId = this[indexOfFirst { it == string }] }

        if (auth.currentUser != null) viewModel.addDiagnosis(string)

        val displayDiseaseIntent = Intent(this@MainActivity, DisplayDiseaseActivity::class.java)
        displayDiseaseIntent.putExtra("disease_id", diseaseId)
        startActivity(displayDiseaseIntent)
    }

}

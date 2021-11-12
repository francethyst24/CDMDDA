package com.example.cdmdda

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cdmdda.adapters.CropAdapter
import com.example.cdmdda.databinding.ActivityMainBinding
import com.example.cdmdda.dto.Crop
import com.example.cdmdda.fragments.LogoutFragment
import com.example.cdmdda.viewmodels.MainViewModel
//import com.example.cdmdda.ml.AlModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity()
    , LogoutFragment.LogoutFragmentListener
    , CropAdapter.OnItemClickListener
{

    // region -- declare: ViewBinding, ViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    // endregion

    // region -- declare: Firebase - Auth and Firestore
    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore = Firebase.firestore
    // private val cropRef : CollectionReference = db.collection("crops")
    // endregion

    // declare: FirestoreRecyclerAdapter
    private var cropAdapter: CropAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // region -- init: ViewBinding, ViewModel, Toolbar
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setSupportActionBar(binding.toolbarMain)
        setContentView(binding.root)
        // endregion

//bell was here

        binding.buttonCamera.setOnClickListener{ }



        // region -- init: Firebase - Auth
        auth = Firebase.auth
        // endregion

        setRecyclerView()
    }

    // init: RecyclerView
    private fun setRecyclerView() {
        cropAdapter = CropAdapter(viewModel.options)
        binding.rcvCrops.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = cropAdapter
        }

        cropAdapter!!.setOnItemClickListener(this@MainActivity)
    }

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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart() {
        super.onStart()
        // START data flow
        cropAdapter?.startListening()

        // region -- update: UI -> authStateChanged
        binding.textUserId.text = if (viewModel.reload()) {
            viewModel.userEmail
        } else { this@MainActivity.getString(R.string.text_guest) }
        // endregion

    }

    override fun onStop() {
        super.onStop()
        // STOP data flow - avoid memory leaks
        cropAdapter?.stopListening()
    }

    // region -- events: logoutDialogFragment
    override fun onLogoutClick(fragment: AppCompatDialogFragment) {
        auth.signOut()
        finish(); startActivity(this@MainActivity.intent)
        Toast.makeText(this@MainActivity, "Logged out", Toast.LENGTH_SHORT).show()
    }

    override fun onCancelClick(fragment: AppCompatDialogFragment) {}
    // endregion

    // events: rcvItemsOnClick
    override fun onItemClick(documentSnapshot: DocumentSnapshot, position: Int) {
        // val crop = documentSnapshot.toObject(Crop::class.java)
        val displayCropIntent = Intent(
            this@MainActivity, DisplayCropActivity::class.java)
        /*
        documentSnapshot.apply {
            Toast.makeText(this@MainActivity,
                "Pos $position | ID: $id", Toast.LENGTH_SHORT).show()
        }
        */
        displayCropIntent.putExtra("crop_id", documentSnapshot.id)
        startActivity(displayCropIntent)
    }


    /*
    private val cPermissionLauncher = registerForActivityResult(RequestPermission())
    { isGranted: Boolean -> if (isGranted) buttonCamera.callOnClick() }

    private val cActivityLauncher = registerForActivityResult(TakePicturePreview())
    { result: Bitmap ->
        if (result != null) {
            binding.imgPreview.setImageBitmap(result);
            feedToTFLiteModel(result)
        }
    }

    //  endregion

    private fun feedToTFLiteModel(bitmap: Bitmap) {
        val dim = 224
        try {
            val rescaledBitmap = createScaledBitmap(bitmap, dim, dim, true)
            val tensorImage = TensorImage.createFrom(
                TensorImage.fromBitmap(rescaledBitmap), DataType.FLOAT32
            )
            val model = AlModel.newInstance(Objects.requireNonNull(this@MainActivity))

            // Create inputs for reference.
            val inputFeature0 = TensorBuffer.createFixedSize(
                intArrayOf(1, dim, dim, 3), DataType.FLOAT32
            )
            inputFeature0.loadBuffer(tensorImage.buffer)

            // Runs model inference and gets result.
            val outputFeature0: TensorBuffer = model.process(inputFeature0)
                .outputFeature0AsTensorBuffer

            // Releases model resources if no longer used.
            model.close()

            openResultDialog(outputFeature0.floatArray)
        } catch (e: Exception) { e.printStackTrace() }

    }

    private fun openResultDialog(probabilities: FloatArray) {
        val diagnosis = StringBuilder()
        var topPScore = 0f; var topPIndex = -1

        for (i in probabilities.indices) {
            diagnosis.apply {
                append("|")
                append(String.format(
                    Locale.getDefault(), "%3.2f", probabilities[i] * 100)
                )
            }
            if (probabilities[i] > topPScore) {
                topPScore = probabilities[i]; topPIndex = i
            }
        }

        diagnosis.append(" ").append(topPIndex + 1)

        val builder = AlertDialog.Builder(this@MainActivity)
        builder.apply {
            setTitle(getString(R.string.dialog_title_result))
            setMessage(diagnosis.toString())
            setPositiveButton(getString(R.string.dialog_button_ok), null)
            create().show()
        }
    }

    // */
}

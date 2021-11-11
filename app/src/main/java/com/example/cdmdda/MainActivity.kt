package com.example.cdmdda

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cdmdda.adapters.CropAdapter
import com.example.cdmdda.databinding.ActivityMainBinding
import com.example.cdmdda.dto.Crop
import com.example.cdmdda.fragments.LogoutFragment
//import com.example.cdmdda.ml.AlModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.util.*

class MainActivity : AppCompatActivity()
    , LogoutFragment.LogoutFragmentListener
    , CropAdapter.OnItemClickListener
{

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private lateinit var auth: FirebaseAuth
    private val db: FirebaseFirestore = Firebase.firestore
    // private val cropRef : CollectionReference = db.collection("crops")

    private var cropAdapter: CropAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // region -- ViewBinding init
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        setContentView(binding.root)
        // endregion

//bell was here
        // region -- ToolBar init
        setSupportActionBar(binding.toolbarMain)
        // endregion

        binding.buttonCamera.setOnClickListener{ }


        // region -- Firebase init
        auth = Firebase.auth
        // endregion

        setRecyclerView()
    }

    private fun setRecyclerView() {
        cropAdapter = CropAdapter(viewModel.options)
        binding.rcvCrops.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = cropAdapter
        }

        cropAdapter!!.setOnItemClickListener(this@MainActivity)
    }

    // HANDLE menu events
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

    // INFLATE menu to actionBar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // UPDATE on auth state change
    override fun onStart() {
        super.onStart()
        // START data flow
        cropAdapter?.startListening()

        // region -- UPDATE UI on authentication
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

    // region -- HANDLE logoutDialogFragment events
    override fun onLogoutClick(fragment: AppCompatDialogFragment) {
        auth.signOut()
        finish(); startActivity(this@MainActivity.intent)
        Toast.makeText(this@MainActivity, "Logged out", Toast.LENGTH_SHORT).show()
    }

    override fun onCancelClick(fragment: AppCompatDialogFragment) {}
    // endregion


    override fun onItemClick(documentSnapshot: DocumentSnapshot, position: Int) {
        val crop = documentSnapshot.toObject(Crop::class.java)
        documentSnapshot.apply {
            Toast.makeText(this@MainActivity,
                "Pos $position | ID: $id", Toast.LENGTH_SHORT).show()
        }
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

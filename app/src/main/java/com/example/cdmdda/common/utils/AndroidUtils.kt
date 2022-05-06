package com.example.cdmdda.common.utils

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Parcelable
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cdmdda.common.BundleCallback
import com.example.cdmdda.common.Constants
import com.example.cdmdda.common.Constants.ARRAY
import com.example.cdmdda.common.StringArray
import com.example.cdmdda.presentation.CropProfileActivity
import com.example.cdmdda.presentation.DiseaseProfileActivity
import com.example.cdmdda.presentation.MainActivity
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object AndroidUtils {
    // region // FragmentResult
    const val VIEW_MODEL_CLASS = "view_model_class"
    const val REQ = "_request"
    const val RES = "_result"

    const val SHOW_DIAGNOSIS_REQUEST = "diagnosis$REQ"
    const val SHOW_DIAGNOSIS_RESULT = "diagnosis$RES"
    const val CLEAR_DIAGNOSIS_REQUEST = "clear_diagnosis$REQ"
    const val CLEAR_DIAGNOSIS_RESULT = "clear_diagnosis$RES"
    const val CLEAR_SEARCH_QUERY_REQUEST = "clear_search_query$REQ"
    const val CLEAR_SEARCH_QUERY_RESULT = "clear_search_query$RES"

    const val LOGOUT_REQUEST = "logout$REQ"
    const val LOGOUT_RESULT = "logout$RES"

    const val LOGIN_REQUEST = "login$REQ"
    const val LOGIN_RESULT = "login$RES"
    const val REGISTER_REQUEST = "register$REQ"
    const val REGISTER_RESULT = "register$RES"

    fun FragmentManager.setResultListener(lifecycleOwner: LifecycleOwner, request: String, result: BundleCallback) {
        setFragmentResultListener(request, lifecycleOwner) { _, bundle -> result(bundle) }
    }

    // endregion

    // region // ActivityResult
    const val INPUT_IMAGE = "image/*"
    const val PERMISSION_GRANTED = PackageManager.PERMISSION_GRANTED

    fun Context.checkSelfPermissionCompat(permission: String): Int {
        return ContextCompat.checkSelfPermission(this, permission)
    }
    // endregion

    // region // Toast

    fun Context.toast(msg: String, len: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, msg, len).show()

    fun Context.toast(resId: Int, len: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(this, getString(resId), len).show()
    }
    // endregion

    // region // Intent
    const val FLAG_ACTIVITY_CLEAR_TOP = Intent.FLAG_ACTIVITY_CLEAR_TOP

    fun <T> Context.intent(activity: Class<T>): Intent {
        return Intent(this, activity)
    }

    fun Activity.restartMain() {
        finishAffinity()
        startActivity(applicationContext.intent(MainActivity::class.java))
    }

    fun Activity.restartCurrent() {
        restartMain()
        startActivity(intent)
    }

    fun Context.intentWith(extra: String? = null, parcel: Parcelable? = null): Intent = when (extra) {
        Constants.CROP -> intent(CropProfileActivity::class.java).putExtra(extra, parcel)
        Constants.DISEASE -> intent(DiseaseProfileActivity::class.java).putExtra(extra, parcel)
        else -> (this as Activity).intent
    }
    // endregion

    fun Context.getAssetPath(fileName: String): String? {
        val file = File(filesDir, fileName)
        try {
            val inpStream: InputStream = assets.open(fileName)
            try {
                val outStream = FileOutputStream(file, false)
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (true) {
                    read = inpStream.read(buffer)
                    if (read == -1) break
                    outStream.write(buffer, 0, read)
                }
                outStream.flush()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun <T> Context.getComponentName(cls: Class<T>) = ComponentName(this, cls)

    // region // Resources
    const val UI_MODE_NIGHT_MASK   = Configuration.UI_MODE_NIGHT_MASK
    const val UI_MODE_NIGHT_YES    = Configuration.UI_MODE_NIGHT_YES
    const val ORIENTATION_LANDSCAPE = Configuration.ORIENTATION_LANDSCAPE

    fun Context.getResourceId(type: String, name: String): Int {
        return resources.getIdentifier("@$type/$name", type, packageName)
    }

    fun Context.getStringArrayBy(name: String): StringArray {
        return try {
            resources.getStringArray(getResourceId(ARRAY, name))
        } catch (e: Resources.NotFoundException) {
            arrayOf()
        }
    }

    fun Context.getStringArray(@ArrayRes id: Int): StringArray {
        return try {
            resources.getStringArray(id)
        } catch (e: Resources.NotFoundException) {
            arrayOf()
        }
    }

    fun Context.getStringCompat(@StringRes id: Int) = try {
        getString(id)
    } catch (e: Resources.NotFoundException) {
        String()
    }

    fun Context.getColorCompat(@ColorRes id: Int) = ContextCompat.getColor(this, id)
    // endregion

    // region // LiveData
    fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
        observe(owner, object : Observer<T> {
            override fun onChanged(value: T) {
                removeObserver(this)
                observer(value)
            }
        })
    }
    // endregion

    // region // RecyclerView
    const val ORIENTATION_Y = LinearLayoutManager.VERTICAL
    const val ORIENTATION_X = LinearLayoutManager.HORIZONTAL

    fun RecyclerView.setDefaults(hasFixedSize: Boolean = false, enabled: Boolean = false) {
        setHasFixedSize(hasFixedSize)
        isNestedScrollingEnabled = enabled
    }
    // endregion

    // region // TextView
    const val HINT_COLOR = "hint_color"
    const val TEXT_COLOR = "text_color"
    // endregion

    // region // MenuItem
    const val SHOW_AS_ACTION_NEVER = MenuItem.SHOW_AS_ACTION_NEVER
    // endregion

    // region // TextInputLayout
    fun TextInputLayout.doErrorClearOnTextChanged() {
        editText?.doOnTextChanged { _, _, _, _ -> if (isErrorEnabled) error = null }
    }
    // endregion
}
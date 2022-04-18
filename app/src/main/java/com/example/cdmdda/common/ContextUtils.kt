package com.example.cdmdda.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.example.cdmdda.common.Constants.TOAST_SHORT
import com.example.cdmdda.presentation.CropProfileActivity
import com.example.cdmdda.presentation.DiseaseProfileActivity
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ContextUtils {
    fun Context.toast(msg: String, len: Int = TOAST_SHORT) = Toast.makeText(this, msg, len).show()

    fun Context.getAssetPath(name: String): String? {
        val file = File(filesDir, name)
        try {
            val inpStream: InputStream = assets.open(name)
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

    fun <T> Context.intent(activity: Class<T>) = Intent(this, activity)

    fun Context.intentWith(extra: String? = null, parcel: Parcelable? = null): Intent = when (extra) {
        Constants.CROP -> intent(CropProfileActivity::class.java).putExtra(extra, parcel)
        Constants.DISEASE -> intent(DiseaseProfileActivity::class.java).putExtra(extra, parcel)
        else -> (this as Activity).intent
    }

    fun Context.getColorCompat(@ColorRes id: Int) = ContextCompat.getColor(this, id)
}
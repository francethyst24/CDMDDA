package com.example.cdmdda.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.cdmdda.R
import com.example.cdmdda.databinding.ActivitySettingsBinding
import com.example.cdmdda.view.fragment.ClearDiagnosisDialog
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SettingsActivity : AppCompatActivity() {

    private lateinit var layout : ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layout = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(layout.root)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment(supportFragmentManager))
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment(private val supportFragmentManager: FragmentManager) : PreferenceFragmentCompat(), ClearDiagnosisDialog.ClearDiagnosisDialogListener {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val clearDiagnosisPreference: Preference? = findPreference("clear_diagnosis")
            val clearSearchPreference: Preference? = findPreference("clear_search")

            clearDiagnosisPreference?.setOnPreferenceClickListener {
                ClearDiagnosisDialog().show(supportFragmentManager, "SettingsActivity")
                true
            }


        }

        override fun onClearDiagnosisClick(fragment: AppCompatDialogFragment) {
            val db = Firebase.firestore; val auth = Firebase.auth
            val query = db.collection("diagnosis").whereEqualTo("user_id", auth.uid)
        }
    }
}
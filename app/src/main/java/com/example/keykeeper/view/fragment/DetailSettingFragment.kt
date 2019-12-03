package com.example.keykeeper.view.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.example.keykeeper.R

class DetailSettingFragment:PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}
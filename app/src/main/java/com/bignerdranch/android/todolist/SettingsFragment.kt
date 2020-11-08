package com.bignerdranch.android.todolist

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.*

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var darkThemeSwitchPreferenceCompat: SwitchPreferenceCompat
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        darkThemeSwitchPreferenceCompat = findPreference("Theme")!!
        darkThemeSwitchPreferenceCompat.isChecked =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        preference.key
        when(preference.key){
            "Theme" -> {
                darkThemeSwitchPreferenceCompat = findPreference(preference.key)!!
                if (darkThemeSwitchPreferenceCompat.isChecked)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                Log.d("pref", "${darkThemeSwitchPreferenceCompat.isChecked}")
            }
            else -> true
        }
        return super.onPreferenceTreeClick(preference)
    }

}
package com.example.eventapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.example.eventapp.R
import com.example.eventapp.di.Injection
import com.example.eventapp.ui.SettingPreferences
import com.example.eventapp.ui.dataStore
import com.example.eventapp.ui.viewmodels.MainViewModel
import com.example.eventapp.ui.viewmodels.ViewModelFactory

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var viewModel: MainViewModel

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // Initialize viewmodel
        val eventRepository = Injection.provideRepository(requireContext())
        val preferences = SettingPreferences.getInstance(requireContext().dataStore)
        val factory = ViewModelFactory(eventRepository, preferences)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchTheme = findPreference<SwitchPreference>("theme")

        // Observe the theme setting and update the switch state
        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            switchTheme?.isChecked = isDarkModeActive
            applyDarkMode(isDarkModeActive)
        }

        // Listen to switch changes and save the new theme setting
        switchTheme?.setOnPreferenceChangeListener { _, newValue ->
            val isDarkModeEnabled = newValue as Boolean
            viewModel.saveThemeSetting(isDarkModeEnabled)
            applyDarkMode(isDarkModeEnabled)
            true
        }

    }

    private fun applyDarkMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

}
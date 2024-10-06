package com.example.eventapp.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.eventapp.R
import com.example.eventapp.ui.MyWorker
import com.example.eventapp.ui.viewmodels.MainViewModel
import com.example.eventapp.ui.viewmodels.ViewModelFactory
import java.util.concurrent.TimeUnit

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var workManager: WorkManager
    private lateinit var periodicWorkRequest: PeriodicWorkRequest

    private val viewModel by viewModels<MainViewModel>{
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workManager = WorkManager.getInstance(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchTheme = findPreference<SwitchPreference>("theme")
        val switchNotification = findPreference<SwitchPreference>("notification")

        // Observe the theme setting and update the switch state
        viewModel.getThemeSettings().observe(viewLifecycleOwner) {
            switchTheme?.isChecked = it
        }

        // Observe the notification setting and update the switch state
        viewModel.getNotificationSettings().observe(viewLifecycleOwner) {
            switchNotification?.isChecked = it
        }

        // Listen to switch changes and save the new theme setting
        switchTheme?.setOnPreferenceChangeListener { _, newValue ->
            val isDarkModeEnabled = newValue as Boolean
            viewModel.saveThemeSetting(isDarkModeEnabled)
            applyDarkMode(isDarkModeEnabled)
            true
        }

        // Listen to switch changes and save the new notification setting
        switchNotification?.setOnPreferenceChangeListener { _, newValue ->
            val isDailyReminderEnabled = newValue as Boolean
            viewModel.saveNotificationSetting(isDailyReminderEnabled)
            applyDailyReminder(isDailyReminderEnabled)
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

    private fun applyDailyReminder(isDailyReminder: Boolean) {
        if (isDailyReminder) {
            startPeriodicTask()
        } else {
            workManager.cancelUniqueWork("notification")
        }
    }

    private fun startPeriodicTask() {
        val data = Data.Builder()
            .putString(MyWorker.EXTRA_EVENT, "haloo")
            .build()
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        periodicWorkRequest =
            PeriodicWorkRequest.Builder(MyWorker::class.java, 24, TimeUnit.HOURS)
                .setInputData(data)
                .setConstraints(constraints)
                .build()
        workManager.enqueueUniquePeriodicWork(
            "notification",
            ExistingPeriodicWorkPolicy.KEEP,
            periodicWorkRequest
        )
        workManager.getWorkInfosForUniqueWorkLiveData("notification")
            .observe(requireActivity()) { workInfo ->
                workInfo.forEach {
                    Log.d("test", it.state.toString())
                }
            }
    }

}
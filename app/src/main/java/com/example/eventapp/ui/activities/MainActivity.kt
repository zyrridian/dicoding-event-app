package com.example.eventapp.ui.activities

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.eventapp.R
import com.example.eventapp.databinding.ActivityMainBinding
import com.example.eventapp.di.Injection
import com.example.eventapp.ui.SettingPreferences
import com.example.eventapp.ui.dataStore
import com.example.eventapp.ui.dialogs.NetworkDialog
import com.example.eventapp.ui.viewmodels.MainViewModel
import com.example.eventapp.ui.viewmodels.ViewModelFactory
import com.example.eventapp.utils.NetworkUtil
import com.example.eventapp.utils.NetworkUtil.isNetworkAvailable
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var networkChangeReceiver: NetworkUtil.NetworkChangeReceiver
    private var networkDialog: NetworkDialog? = null
    private var dataRefreshListener: NetworkChangeListener? = null
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        val eventRepository = Injection.provideRepository(this)
        val settingPreferences = SettingPreferences.getInstance(dataStore)
        val factory = ViewModelFactory(eventRepository, settingPreferences)
        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]

        // Observe theme setting and apply it
        viewModel.getThemeSettings().observe(this) { isDarkModeActive ->
            applyDarkMode(isDarkModeActive)
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setupBottomNavigation()
        }

        networkDialog = NetworkDialog(this)
        setupNetworkChangeReceiver()
    }

    private fun setupBottomNavigation() {
        val bottomNav: BottomNavigationView = binding.bottomNav
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        bottomNav.setupWithNavController(navController)
    }

    private fun applyDarkMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun setupNetworkChangeReceiver() {
        networkChangeReceiver = NetworkUtil.NetworkChangeReceiver { isConnected ->
            if (!isConnected) {
                networkDialog?.showNoInternetDialog {
                    if (isNetworkAvailable(this)) {
                        networkDialog?.dismissDialog()
                        dataRefreshListener?.onNetworkChanged()
                    } else {
                        Toast.makeText(this, "No Internet Connection!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                networkDialog?.dismissDialog()
                dataRefreshListener?.onNetworkChanged()
            }
        }
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkChangeReceiver, filter)
    }

//    override fun onStart() {
//        super.onStart()
//        registerReceiver(networkChangeReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
//    }

//    override fun onStop() {
//        super.onStop()
//        unregisterReceiver(networkChangeReceiver)
//    }

    override fun onDestroy() {
        super.onDestroy()
        networkDialog?.dismissDialog()
    }

    fun setOnDataRefreshListener(listener: NetworkChangeListener) {
        dataRefreshListener = listener
    }

    interface NetworkChangeListener {
        fun onNetworkChanged()
    }

}
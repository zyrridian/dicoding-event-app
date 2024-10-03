package com.example.eventapp.ui.activities

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.eventapp.R
import com.example.eventapp.databinding.ActivityMainBinding
import com.example.eventapp.ui.dialogs.NetworkDialog
import com.example.eventapp.utils.NetworkUtil
import com.example.eventapp.utils.NetworkUtil.isNetworkAvailable
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var networkChangeReceiver: NetworkUtil.NetworkChangeReceiver
    private var networkDialog: NetworkDialog? = null
    private var dataRefreshListener: NetworkChangeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            // initialize something if you want, like viewmodel or something
            // keepOnScreenCondition { }
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkDialog = NetworkDialog(this)
        setupNetworkChangeReceiver()

        val bottomNav: BottomNavigationView = binding.bottomNav
        val navController = findNavController(R.id.nav_host_fragment_container)

        bottomNav.setupWithNavController(navController)

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
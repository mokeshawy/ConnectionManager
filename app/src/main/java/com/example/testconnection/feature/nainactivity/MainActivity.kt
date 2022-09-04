package com.example.testconnection.feature.nainactivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.testconnection.R
import com.example.testconnection.core.network_manager.NetworkManager
import com.example.testconnection.core.utils.Constants.showToast
import com.example.testconnection.databinding.ActivityMainBinding
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var networkManager: NetworkManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initNetworkManager()


        binding.checkBtn.setOnClickListener {
            checkOnInternetAccess()
        }
    }

    private fun checkOnNetworkStatus() {
        if (networkManager.isAvailable) {
            showToast(this, "Network is Available")
        } else {
            showToast(this, "Network is not Available")
        }
    }

    private fun checkOnInternetAccess() {
        if (networkManager.isInternetAccess) {
            showToast(this, "Internet is Available")
        } else {
            showToast(this, "Internet is not Available")
        }
    }

    private fun initNetworkManager() {
        networkManager = NetworkManager(this)
    }
}
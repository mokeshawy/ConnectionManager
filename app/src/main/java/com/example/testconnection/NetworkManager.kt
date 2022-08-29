package com.example.testconnection

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class NetworkManager(
    private val activity: AppCompatActivity,
) {

    private var networkCapabilities: NetworkCapabilities? = null
    private var getNetworkRequest = getNetworkRequest()
    private var networkCallback = getNetworkCallBack()
    private val telephonyManager
        get() =
            activity.getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager

    init {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                PhoneStateListener().registerPhoneStateListener(activity, telephonyManager)
            }

            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                getConnectivityManager().registerNetworkCallback(getNetworkRequest, networkCallback)
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                getConnectivityManager().unregisterNetworkCallback(networkCallback)
            }
        })
    }

    private fun getNetworkRequest(): NetworkRequest {
        return NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
    }


    private fun getNetworkCallBack(): ConnectivityManager.NetworkCallback {
        return object : ConnectivityManager.NetworkCallback() {


            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                networkCapabilities = getConnectivityManager().getNetworkCapabilities(network)
                checkConnectInternetType()
            }


            override fun onLost(network: Network) {    //when Wifi 【turns off】
                super.onLost(network)
                checkDisconnectInternetType()
            }
        }
    }

    private fun getConnectivityManager() =
        activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    private fun checkConnectInternetType() {
        when {
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> {
                showToast("Cellular is on")
                getSignalStrengthFromApi29ToUp()
            }
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ->
                showToast("Wifi is on")
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true ->
                showToast("Ethernet on")
        }
    }

    private fun checkDisconnectInternetType() {
        when {
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> {
                showToast("Cellular is off")
            }
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ->
                showToast("Wifi off")
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true ->
                showToast("Ethernet off")
        }
    }


    private fun getSignalStrengthFromApi29ToUp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            showToast("${telephonyManager.signalStrength?.level}")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}
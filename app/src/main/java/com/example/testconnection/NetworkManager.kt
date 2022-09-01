package com.example.testconnection

import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.*
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.TELEPHONY_SERVICE
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

const val SIGNAL_STRENGTH_LIMITED_NUMBER = 5

class NetworkManager(
    private val activity: AppCompatActivity,
) {

    private var networkCapabilities: NetworkCapabilities? = null
    private var getNetworkRequest = getNetworkRequest()
    private var networkCallback = getNetworkCallBack()
    private val telephonyManager
        get() =
            activity.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
    private val wifiManager
        get() =
            activity.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager

    init {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                registerPhoneStateListener()
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

    private fun registerPhoneStateListener() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) return
        PhoneStateListener().registerPhoneStateListener(activity, telephonyManager)
    }

    private fun getNetworkRequest(): NetworkRequest {
        return NetworkRequest.Builder()
            .addTransportType(TRANSPORT_WIFI)
            .addTransportType(TRANSPORT_CELLULAR)
            .addTransportType(TRANSPORT_ETHERNET)
            .build()
    }


    private fun getNetworkCallBack(): ConnectivityManager.NetworkCallback {
        return object : ConnectivityManager.NetworkCallback() {


            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                networkCapabilities = getConnectivityManager().getNetworkCapabilities(network)
                checkConnectInternetType()
            }


            override fun onLost(network: Network) {
                super.onLost(network)
                checkDisconnectInternetType()
            }
        }
    }

    private fun getConnectivityManager() =
        activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    private fun checkConnectInternetType() {
        when {
            networkCapabilities?.hasTransport(TRANSPORT_CELLULAR) == true -> {
                getGsmSignalStrengthGreaterThanApi28()
                getSimOperatorName()
                Log.e("Cellular is on","On")
                //here log cellular turn on
            }
            networkCapabilities?.hasTransport(TRANSPORT_WIFI) == true -> {
                showToast("Wifi is on")
                getWifiSignalStrengthFromApi30OrGreaterThanApi30()
                getWifiSignalStrengthLessThanApi30()
                // here log wifi turn on
            }
            networkCapabilities?.hasTransport(TRANSPORT_ETHERNET) == true ->
                showToast("Ethernet on")
            //here log ether net turn on
        }
    }

    private fun checkDisconnectInternetType() {
        when {
            networkCapabilities?.hasTransport(TRANSPORT_CELLULAR) == true ->
                showToast("Cellular is off")
            //here log cellular turn off
            networkCapabilities?.hasTransport(TRANSPORT_WIFI) == true ->
                showToast("Wifi off")
            // here log wifi turn off
            networkCapabilities?.hasTransport(TRANSPORT_ETHERNET) == true ->
                showToast("Ethernet off")
            //here log ether net turn off
        }
    }


    private fun getGsmSignalStrengthGreaterThanApi28() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
            showToast("signal strength greater than Api 28: ${telephonyManager.signalStrength?.level}")
        //log signal strength greater than Api 28
    }

    private fun getSimOperatorName() {
        showToast("Network Name: ${telephonyManager.simOperatorName}")
        //here log sim operator name
    }

    private fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }


    private fun getWifiSignalStrengthFromApi30OrGreaterThanApi30() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            val level = wifiManager.calculateSignalLevel(SIGNAL_STRENGTH_LIMITED_NUMBER)
            showToast("Wifi signal equal or greater than Api 30: $level")
            //here log Wifi signal equal or greater than Api 30
        }
    }

    @Suppress("DEPRECATION")
    private fun getWifiSignalStrengthLessThanApi30() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            val rssi = wifiManager.connectionInfo.rssi
            val level = WifiManager.calculateSignalLevel(rssi, SIGNAL_STRENGTH_LIMITED_NUMBER)
            showToast("Wifi signal less than Api 30: $level")
            //here Wifi signal less than Api 30
        }
    }
}
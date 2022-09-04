package com.example.testconnection.core.network_manager

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.*
import android.net.NetworkRequest
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.testconnection.core.network_manager.telephony_manager.TelephonyManager
import com.example.testconnection.core.network_manager.wifi_manager.WifiManager
import com.example.testconnection.core.utils.Constants.showToast
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import javax.inject.Inject

const val SIGNAL_STRENGTH_LIMITED_NUMBER = 5
const val HOST = "192.168.1.1"

class NetworkManager @Inject constructor(
    private val activity: Activity,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    var isAvailable = false
    var isInternetAccess = false
    private var networkCapabilities: NetworkCapabilities? = null
    private var getNetworkRequest = getNetworkRequest()
    private var networkCallback = getNetworkCallBack()
    private val appCompatActivity get() = (activity as AppCompatActivity)

    @Inject
    lateinit var telephonyManager: TelephonyManager

    @Inject
    lateinit var wifiManager: WifiManager

    init {
        appCompatActivity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                telephonyManager = TelephonyManager(activity)
                wifiManager = WifiManager(activity)
                telephonyManager.registerPhoneStateListener()
            }

            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                appCompatActivity.lifecycleScope.launch { checkOnInternetAccess() }
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
            .addTransportType(TRANSPORT_WIFI)
            .addTransportType(TRANSPORT_CELLULAR)
            .addTransportType(TRANSPORT_ETHERNET)
            .build()
    }


    private fun getNetworkCallBack(): ConnectivityManager.NetworkCallback {
        return object : ConnectivityManager.NetworkCallback() {


            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                isAvailable = true
                networkCapabilities = getConnectivityManager().getNetworkCapabilities(network)
                checkConnectInternetType()
            }


            override fun onLost(network: Network) {
                super.onLost(network)
                isAvailable = false
                checkDisconnectInternetType()
            }
        }
    }

    private fun getConnectivityManager() =
        activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager


    private fun checkConnectInternetType() {
        networkCapabilities?.let {
            when {
                it.hasTransport(TRANSPORT_CELLULAR) -> {
                    telephonyManager.getGsmSignalStrengthGreaterThanApi28()
                    telephonyManager.getSimOperatorName()
                    Log.e("Cellular is on", "On")
                    //cellular turn on
                }
                it.hasTransport(TRANSPORT_WIFI) -> {
                    showToast(activity, "Wifi is on")
                    wifiManager.getWifiSignalStrengthFromApi30OrGreaterThanApi30()
                    wifiManager.getWifiSignalStrengthLessThanApi30()
                    //wifi turn on
                }
                it.hasTransport(TRANSPORT_ETHERNET) -> {
                    showToast(activity, "Ethernet on")
                    //ether net turn on
                }
                else -> {
                    // nothing handling
                }
            }
        }
    }

    private fun checkDisconnectInternetType() {
        networkCapabilities?.let {
            when {
                it.hasTransport(TRANSPORT_CELLULAR) -> {
                    showToast(activity, "Cellular is off")
                    //cellular turn off
                }
                it.hasTransport(TRANSPORT_WIFI) -> {
                    showToast(activity, "Wifi off")
                    //wifi turn off
                }
                it.hasTransport(TRANSPORT_ETHERNET) -> {
                    showToast(activity, "Ethernet off")
                    //here ether net turn off
                }
            }
        }
    }

    suspend fun checkOnInternetAccess() {
        withContext(dispatcher) {
            val inetAddress = InetAddress.getByName(HOST)
            isInternetAccess = inetAddress.isReachable(100)
        }
    }
}
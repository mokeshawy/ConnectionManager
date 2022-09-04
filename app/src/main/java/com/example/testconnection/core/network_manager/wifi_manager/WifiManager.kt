package com.example.testconnection.core.network_manager.wifi_manager

import android.app.Activity
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import com.example.testconnection.core.network_manager.SIGNAL_STRENGTH_LIMITED_NUMBER
import com.example.testconnection.core.utils.Constants
import com.example.testconnection.core.utils.Constants.showToast
import javax.inject.Inject

class WifiManager @Inject constructor(private val activity: Activity) {

    private val wifiManager
        get() =
            activity.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    fun getWifiSignalStrengthFromApi30OrGreaterThanApi30() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val level = wifiManager.calculateSignalLevel(SIGNAL_STRENGTH_LIMITED_NUMBER)
            showToast(activity, "Wifi signal equal or greater than Api 30: $level")
            //Wifi signal equal or greater than Api 30
        }
    }

    @Suppress("DEPRECATION")
    fun getWifiSignalStrengthLessThanApi30() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            val rssi = wifiManager.connectionInfo.rssi
            val level = WifiManager.calculateSignalLevel(rssi, SIGNAL_STRENGTH_LIMITED_NUMBER)
            showToast(activity, "Wifi signal less than Api 30: $level")
            //Wifi signal less than Api 30
        }
    }
}
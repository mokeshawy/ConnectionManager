package com.example.testconnection.core.network_manager.telephony_manager

import android.app.Activity
import android.os.Build
import android.telephony.TelephonyManager
import androidx.appcompat.app.AppCompatActivity
import com.example.testconnection.core.network_manager.PhoneStateListener
import com.example.testconnection.core.utils.Constants.showToast
import javax.inject.Inject

class TelephonyManager @Inject constructor(private val activity: Activity) {

    private val telephonyManager
        get() =
            activity.getSystemService(AppCompatActivity.TELEPHONY_SERVICE) as TelephonyManager

    fun registerPhoneStateListener() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) return
        PhoneStateListener().registerPhoneStateListener(activity, telephonyManager)
    }

    fun getGsmSignalStrengthGreaterThanApi28() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
            showToast(activity,
                "signal strength greater than Api 28: ${telephonyManager.signalStrength?.level}")
        //signal strength greater than Api 28
    }

    fun getSimOperatorName() {
        showToast(activity, "Network Name: ${telephonyManager.simOperatorName}")
    }
}
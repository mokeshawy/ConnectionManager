package com.example.testconnection

import android.annotation.SuppressLint
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.SignalStrength
import android.telephony.TelephonyManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

@Suppress("DEPRECATION")
class PhoneStateListener {

    fun registerPhoneStateListener(
        activity: AppCompatActivity,
        telephonyManager: TelephonyManager,
    ) {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_SIGNAL_STRENGTHS)
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
            }
        })
    }

    val phoneStateListener = object : PhoneStateListener() {
        override fun onSignalStrengthsChanged(signalStrength: SignalStrength) {
            super.onSignalStrengthsChanged(signalStrength)
            if (signalStrength.isGsm) {
                signalStrength.apply {
                    getSignalStrengthFromApi21ToApi22()
                    getSignalStrengthFromApi23ToApi28()
                }
            }
        }
    }

    @SuppressLint("LongLogTag")
    private fun SignalStrength.getSignalStrengthFromApi21ToApi22() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.e("Signal Strength from Api 21 to Api 22", "$gsmSignalStrength")
            // here log Signal Strength from Api 21 to Api 22
        }
    }

    @SuppressLint("LongLogTag")
    private fun SignalStrength.getSignalStrengthFromApi23ToApi28() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.e("Signal Strength from Api 23 to Api 28", "$level")
            // here Signal Strength from Api 23 to Api 28
        }
    }
}
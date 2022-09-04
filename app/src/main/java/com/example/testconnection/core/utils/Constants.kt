package com.example.testconnection.core.utils

import android.app.Activity
import android.widget.Toast

object Constants {
    fun showToast(activity: Activity, message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
    }
}
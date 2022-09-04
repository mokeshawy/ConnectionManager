package com.example.testconnection.core.network_manager.di

import android.app.Activity
import com.example.testconnection.core.network_manager.NetworkManager
import com.example.testconnection.core.network_manager.telephony_manager.TelephonyManager
import com.example.testconnection.core.network_manager.wifi_manager.WifiManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
object NetworkManagerModule {

    @Provides
    fun provideNetworkManager(activity: Activity) = NetworkManager(activity)

    @Provides
    fun provideTelephonyManager(activity: Activity) = TelephonyManager(activity)

    @Provides
    fun provideWifiManager(activity: Activity) = WifiManager(activity)
}
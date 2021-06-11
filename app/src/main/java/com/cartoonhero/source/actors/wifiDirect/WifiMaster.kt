package com.cartoonhero.source.actors.wifiDirect

import android.content.Context
import android.net.wifi.WifiManager
import com.cartoonhero.source.actormodel.Actor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class WifiMaster(context: Context): Actor() {
    private val manager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
}
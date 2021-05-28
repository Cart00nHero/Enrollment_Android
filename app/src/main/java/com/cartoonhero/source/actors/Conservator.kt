package com.cartoonhero.source.actors

import android.app.Activity
import androidx.core.app.ActivityCompat
import com.cartoonhero.source.actormodel.Actor
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class Conservator: Actor() {
    private fun beRequestPermission(
        activity: Activity, permissions:Array<String>) {
        CoroutineScope(Dispatchers.Main).launch {
            val permissionId = 1000
            ActivityCompat.requestPermissions(
                activity,
                permissions,
                permissionId
            )
        }
    }
    fun toBeRequestPermission(
        activity: Activity, permissions:Array<String>) {
        send {
            beRequestPermission(activity, permissions)
        }
    }
}
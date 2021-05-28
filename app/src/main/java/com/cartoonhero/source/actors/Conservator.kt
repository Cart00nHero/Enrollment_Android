package com.cartoonhero.source.actors

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
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
    private fun beCheckPermission(
        sender: Actor,context: Context,permissions:String,
        complete:(Boolean) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                permissions
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
                sender.send {
                    complete(false)
                }
            return
        }
        sender.send {
            complete(true)
        }
    }
    fun toBeRequestPermission(
        activity: Activity, permissions:Array<String>) {
        send {
            beRequestPermission(activity, permissions)
        }
    }
    fun toBeCheckPermission(
        sender: Actor, context: Context, permissions:String,
        complete: (Boolean) -> Unit
    ) {
        send {
            beCheckPermission(sender, context, permissions, complete)
        }
    }
}
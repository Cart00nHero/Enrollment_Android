package com.cartoonhero.source.enrollment_android.scene.visitor

import android.Manifest
import android.app.Activity
import android.content.Context
import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.actors.Conservator
import com.cartoonhero.source.actors.p2p.PeerConnector
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi

class VisitorScenario: Actor() {
    private lateinit var peerConnector: PeerConnector
    private fun beCheckPermission(
        context: Context,complete: (Boolean) -> Unit) {
        Conservator().toBeCheckPermission(this,
            context,Manifest.permission.ACCESS_FINE_LOCATION) {
            CoroutineScope(Dispatchers.Main).launch {
                complete(it)
            }
        }
    }
    private fun beRequestPermission(activity: Activity) {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        Conservator().toBeRequestPermission(activity,permission)
    }
    private fun beBuildP2PConnection(
        activity: Activity,complete:(Boolean) -> Unit) {
        peerConnector = PeerConnector(activity)
        peerConnector.toBeSetup(this) {
            complete(it)
        }
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeRequestPermission(activity: Activity) {
        send {
            beRequestPermission(activity)
        }
    }
    fun toBeBuildP2PConnection(
        activity: Activity,complete:(Boolean) -> Unit) {
        send {
            beBuildP2PConnection(activity, complete)
        }
    }
}
package com.cartoonhero.source.enrollment_android.scene.visitor

import android.Manifest
import android.app.Activity
import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.actors.Conservator
import com.cartoonhero.source.actors.p2p.PeerHost
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi

class VisitorScenario: Actor() {
    private lateinit var peerHost: PeerHost
    private fun beRequestPermission(activity: Activity) {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        Conservator().toBeRequestPermission(activity,permission)
    }
    private fun beBuildP2PConnection(
        activity: Activity,complete:(Boolean) -> Unit) {
        peerHost = PeerHost(activity)
        peerHost.toBeBuildConnection(this) {
            complete(it)
        }
    }
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
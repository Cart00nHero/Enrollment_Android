package com.cartoonhero.source.enrollment_android.scene.visitedUnit

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.actors.Conservator
import com.cartoonhero.source.actors.p2p.PeerCommunicator
import com.cartoonhero.source.actors.p2p.PeerConnector
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class UnitScenario: Actor() {
    private var connector:PeerConnector? = null
    private val communicator = PeerCommunicator()
    private val peerList: HashSet<WifiP2pDevice> = hashSetOf()

    private fun beCheckPermission(
        context: Context,complete: (Boolean) -> Unit) {
        Conservator().toBeCheckPermission(this,
            context, Manifest.permission.ACCESS_FINE_LOCATION) {
            CoroutineScope(Dispatchers.Main).launch {
                complete(it)
            }
        }
    }
    private fun beRequestPermission(activity: Activity) {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        Conservator().toBeRequestPermission(activity,permission)
    }

    private fun beSetUpConnection(
        context: Context, complete:(Boolean) -> Unit) {
        connector = PeerConnector(context)
        connector?.toBeSetup(this) {
            if (it) {
                communicator.toBeRun()
                connector?.toBeCreateGroup(this) { success ->
                    CoroutineScope(Dispatchers.Main).launch {
                        complete(success)
                    }
                }
            }
        }
    }
    private fun beSearchPeers() {
        connector?.toBeDiscovering(this) {
            for (peer in it) {
                if (peer.status != WifiP2pDevice.CONNECTED) {
                    connector?.toBeConnectPeer(this,peer) { connected ->
                        if (connected && !peerList.contains(peer)) {
                            peerList.add(peer)
                        }
                    }
                }
            }
        }
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeCheckPermission(
        context: Context,complete: (Boolean) -> Unit) {
        send {
            beCheckPermission(context, complete)
        }
    }
    fun toBeRequestPermission(activity: Activity) {
        send {
            beRequestPermission(activity)
        }
    }
    fun toBeSetUpConnection(
        context: Context, complete:(Boolean) -> Unit) {
        send {
            beSetUpConnection(context, complete)
        }
    }
    fun toBeSearchPeers() {
        send {
            beSearchPeers()
        }
    }
}
package com.cartoonhero.source.enrollment_android.scene.visitedUnit

import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.actors.p2p.PeerCommunicator
import com.cartoonhero.source.actors.p2p.PeerConnector
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class UnitScenario: Actor() {
    private var connector:PeerConnector? = null
    private val communicator = PeerCommunicator()
    private val peerList: HashSet<WifiP2pDevice> = hashSetOf()

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
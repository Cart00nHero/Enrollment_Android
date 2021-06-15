package com.cartoonhero.source.actors.wifiDirect

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.net.wifi.p2p.WifiP2pDevice
import android.os.IBinder
import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.actors.Conservator
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class PeerConnector(context: Context): Actor() {

    private var hostService: WifiP2PService? = null
    private val peers = mutableListOf<WifiP2pDevice>()
    private val mContext: Context = context
    private var serviceBoundEvent: (() -> Unit)? = null

    private fun beSetup(
        sender: Actor,complete: (Boolean) -> Unit) {
        Conservator().toBeCheckPermission(
            this,mContext,
            Manifest.permission.ACCESS_FINE_LOCATION) { granted ->
            if (granted) {
                serviceBoundEvent = {
                    hostService?.isPermissionGranted = granted
                    hostService?.buildConnection {
                        sender.send {
                            complete(it)
                        }
                    }
                }
                bindP2PService()
                return@toBeCheckPermission
            }
            sender.send {
                complete(false)
            }
        }
    }

    private fun beDestroyService() {
        unbindP2PService()
    }

    private fun beDiscovering(
        sender: Actor, complete: (List<WifiP2pDevice>) -> Unit) {
        hostService?.discoverPeers { newPeers ->
            peers.clear()
            peers.addAll(newPeers)
            sender.send {
                complete(peers)
            }
        }
    }
    private fun beStopDiscovering(
        sender: Actor,complete: ((Boolean) -> Unit)?) {
        if (complete != null) {
            hostService?.stopDiscoverPeers {
                sender.send {
                    complete(it)
                }
            }
        }
    }
    private fun beConnectPeer(
        sender: Actor, peer: WifiP2pDevice,
        complete:((Boolean) -> Unit)?) {
        hostService?.connectPeer(peer) {
            if (complete != null) {
                sender.send {
                    complete(it)
                }
            }
        }
    }
    private fun beDisconnect(
        sender: Actor,complete: ((Boolean) -> Unit)?) {
        hostService?.disconnect {
            if (complete != null) {
                sender.send {
                    complete(it)
                }
            }
        }
    }
    private fun beCreateGroup(sender: Actor,complete: (Boolean) -> Unit) {
        hostService?.createGroup {
            sender.send {
                complete(it)
            }
        }
    }
    private fun beRemoveGroup(
        sender: Actor,complete: ((Boolean) -> Unit)?) {
        hostService?.removeGroup {
            if (complete != null) {
                sender.send {
                    complete(it)
                }
            }
        }
    }
    private fun beAskGroupPassphrase(sender: Actor,complete: (String) -> Unit) {
        hostService?.requestGroupInfo {
            sender.send {
                complete(it)
            }
        }
    }

    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeSetup(
        sender: Actor,complete: (Boolean) -> Unit) {
        send {
            beSetup(sender, complete)
        }
    }
    fun toBeDestroyService() {
        send {
            beDestroyService()
        }
    }
    fun toBeDiscovering(
        sender: Actor, complete: (List<WifiP2pDevice>) -> Unit) {
        send {
            beDiscovering(sender, complete)
        }
    }
    fun toBeStopDiscovering(
        sender: Actor,complete: ((Boolean) -> Unit)?) {
        send {
            beStopDiscovering(sender, complete)
        }
    }

    fun toBeConnectPeer(
        sender: Actor, peer: WifiP2pDevice,
        complete:((Boolean) -> Unit)?) {
        send {
            beConnectPeer(sender, peer, complete)
        }
    }
    fun toBeDisconnect(
        sender: Actor,complete: ((Boolean) -> Unit)?) {
        send {
            beDisconnect(sender, complete)
        }
    }
    fun toBeCreateGroup(sender: Actor,complete: (Boolean) -> Unit) {
        send {
            beCreateGroup(sender, complete)
        }
    }
    fun toBeRemoveGroup(
        sender: Actor,complete: ((Boolean) -> Unit)?) {
        send { beRemoveGroup(sender, complete) }
    }

    /* --------------------------------------------------------------------- */
    // MARK: - Private
    private fun bindP2PService() {
        val intent = Intent()
        intent.setClass(mContext,WifiP2PService::class.java)
        mContext.bindService(intent,connection,BIND_AUTO_CREATE)
    }
    private fun unbindP2PService() {
        mContext.unbindService(connection)
    }
    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: WifiP2PService.ServiceBinder = service as WifiP2PService.ServiceBinder
            hostService = binder.wifiService
            serviceBoundEvent?.let { it() }
        }
        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }
}
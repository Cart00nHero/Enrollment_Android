package com.cartoonhero.source.actors.p2p

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
class PeerHost: Actor() {

    private val hostService = P2PHostService()
    private val peers = mutableListOf<WifiP2pDevice>()

    private fun beStart(context: Context) {
        val intent = Intent()
        intent.setClass(context,P2PHostService::class.java)
        context.bindService(intent,connection,BIND_AUTO_CREATE)
    }
    private fun beCheckPermission(
        sender: Actor,context: Context,
        complete: (Boolean) -> Unit) {
        Conservator().toBeCheckPermission(this,context,
            Manifest.permission.ACCESS_FINE_LOCATION) {
            hostService.isPermissionGranted = it
            sender.send {
                complete(it)
            }
        }
    }
    private fun beStop(context: Context) {
        context.unbindService(connection)
    }
    private fun beDiscovering(
        sender: Actor, complete: (List<WifiP2pDevice>) -> Unit) {
        hostService.discoverPeers { newPeers ->
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
            hostService.stopDiscoverPeers {
                sender.send {
                    complete(it)
                }
            }
        }
    }
    private fun beCreateGroup(sender: Actor,complete: (Boolean) -> Unit) {
        hostService.createGroup {
            sender.send {
                complete(it)
            }
        }
    }
    private fun beAskGroupPassphrase(sender: Actor,complete: (String) -> Unit) {
        hostService.requestGroupInfo {
            sender.send {
                complete(it)
            }
        }
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeStart(context: Context) {
        send {
            beStart(context)
        }
    }
    fun toBeCheckPermission(
        sender: Actor,context: Context, complete: (Boolean) -> Unit) {
        send {
            beCheckPermission(sender, context, complete)
        }
    }
    fun toBeStop(context: Context) {
        send {
            beStop(context)
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

    fun toBeAskGroupPassphrase(sender: Actor,complete: (String) -> Unit) {
        send {
            beAskGroupPassphrase(sender, complete)
        }
    }
    fun tobeCreateGroup(sender: Actor,complete: (Boolean) -> Unit) {
        send {
            beCreateGroup(sender, complete)
        }
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Private
    @Suppress("UNREACHABLE_CODE")
    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            TODO("Not yet implemented")
            val binder: P2PHostService.ServiceBinder = service as P2PHostService.ServiceBinder
            hostService = binder.hostService
        }
        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }
}
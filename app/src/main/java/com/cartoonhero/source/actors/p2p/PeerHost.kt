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
class PeerHost(context: Context): Actor() {

    private var hostService: P2PService? = null
    private val peers = mutableListOf<WifiP2pDevice>()
    private val mContext: Context = context
    private var serviceBoundEvent: (() -> Unit)? = null

    private fun beBuildConnection(
        sender: Actor,complete: (Boolean) -> Unit) {
        Conservator().toBeCheckPermission(
            this,mContext,Manifest.permission.ACCESS_FINE_LOCATION) {
            if (it) {
                bindP2PService()
                serviceBoundEvent = {
                    hostService?.isPermissionGranted = it
                    hostService?.buildConnection()
                }
                return@toBeCheckPermission
            }
            sender.send {
                complete(it)
            }
        }
    }

    private fun beDestroyConnection() {
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
    private fun beCreateGroup(sender: Actor,complete: (Boolean) -> Unit) {
        hostService?.createGroup {
            sender.send {
                complete(it)
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
    fun toBeBuildConnection(
        sender: Actor,complete: (Boolean) -> Unit) {
        send {
            beBuildConnection(sender, complete)
        }
    }
    fun toBeDestroyConnection() {
        send {
            beDestroyConnection()
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
    fun toBeCreateGroup(sender: Actor,complete: (Boolean) -> Unit) {
        send {
            beCreateGroup(sender, complete)
        }
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Private
    private fun bindP2PService() {
        val intent = Intent()
        intent.setClass(mContext,P2PService::class.java)
        mContext.bindService(intent,connection,BIND_AUTO_CREATE)
    }
    private fun unbindP2PService() {
        mContext.unbindService(connection)
    }
    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: P2PService.ServiceBinder = service as P2PService.ServiceBinder
            hostService = binder.service
            serviceBoundEvent?.let { it() }
        }
        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }
}
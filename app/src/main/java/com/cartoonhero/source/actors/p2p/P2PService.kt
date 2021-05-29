package com.cartoonhero.source.actors.p2p

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.*
import android.util.Log

class P2PService : Service() {

    private val TAG = "P2PHostService"
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var manager: WifiP2pManager
    private lateinit var receiver: WifiDirectReceiver
    private var discoverPeersEvent: ((List<WifiP2pDevice>) -> Unit)? = null
    var isPermissionGranted = false
    private var isWifiP2pEnabled = false

    override fun onBind(intent: Intent): IBinder {
        return ServiceBinder()
    }
    inner class ServiceBinder : Binder() {
        val service: P2PService
            get() = this@P2PService
    }

    override fun onCreate() {
        super.onCreate()
        val serviceThread = HandlerThread(
            "service_thread",
            Process.THREAD_PRIORITY_BACKGROUND
        )
        serviceThread.start()
        Log.i(TAG, "Service onCreate() is called")
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    fun buildConnection() {
        val intentFilter = IntentFilter()
        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, channelListener)

        receiver = WifiDirectReceiver()
        registerReceiver(receiver, intentFilter)
    }

    @SuppressLint("MissingPermission")
    fun discoverPeers(
        subscribe: (List<WifiP2pDevice>) -> Unit
    ) {
        if (isPermissionGranted) {
            manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    // Code for when the discovery initiation is successful goes here.
                    // No services have actually been discovered yet, so this method
                    // can often be left blank. Code for peer discovery goes in the
                    // onReceive method, detailed below.
                    discoverPeersEvent = subscribe
                }

                override fun onFailure(reason: Int) {
                    // Code for when the discovery initiation fails goes here.
                    // Alert the user that something went wrong.
                    Log.d(TAG, "discover peers error:${reason}")
                }
            })
        }
    }
    fun stopDiscoverPeers(complete: ((Boolean) -> Unit)?) {
        manager.stopPeerDiscovery(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                complete?.let { it(true) }
            }
            override fun onFailure(reason: Int) {
                complete?.let { it(false) }
            }
        })
    }
    @SuppressLint("MissingPermission")
    fun connectPeer(peer: WifiP2pDevice, complete: (Boolean) -> Unit) {
        if (isPermissionGranted) {
            val config = WifiP2pConfig().apply {
                deviceAddress = peer.deviceAddress
                wps.setup = WpsInfo.PBC
            }
            manager.connect(
                channel, config, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        complete(true)
                    }

                    override fun onFailure(reason: Int) {
                        Log.d(TAG, "connect peers error:${reason}")
                        complete(false)
                    }
                })
        }
    }
    fun disconnect(complete: (Boolean) -> Unit) {
        manager.cancelConnect(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                complete(true)
            }

            override fun onFailure(reason: Int) {
                complete(false)
            }
        })
    }
    @SuppressLint("MissingPermission")
    fun requestGroupInfo(complete: (String) -> Unit) {
        if (isPermissionGranted) {
            manager.requestGroupInfo(channel) { group ->
                complete(group.passphrase)
            }
        }
    }
    @SuppressLint("MissingPermission")
    fun createGroup(complete: (Boolean) -> Unit) {
        manager.createGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                complete(true)
            }
            override fun onFailure(reason: Int) {
                Log.d(TAG, "create group error:${reason}")
                complete(false)
            }
        })
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Private
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager: ConnectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Listeners
    private val peersListener = WifiP2pManager.PeerListListener {
        if (discoverPeersEvent != null && it.deviceList.isNotEmpty()) {
            discoverPeersEvent!!(it.deviceList.toList())
        }
    }
    private val connectionListener = WifiP2pManager.ConnectionInfoListener { info ->

        // InetAddress from WifiP2pInfo struct.
        val groupOwnerAddress: String = info.groupOwnerAddress.hostAddress

        // After the group negotiation, we can determine the group owner
        // (server).
        if (info.groupFormed && info.isGroupOwner) {
            // Do whatever tasks are specific to the group owner.
            // One common case is creating a group owner thread and accepting
            // incoming connections.
        } else if (info.groupFormed) {
            // The other device acts as the peer (client). In this case,
            // you'll want to create a peer thread that connects
            // to the group owner.
        } else {
            Log.d(TAG,"還有else")
        }
    }


    private val channelListener = WifiP2pManager.ChannelListener {
        // The channel to the framework has been disconnected.
    }

    private inner class WifiDirectReceiver : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                    // Determine if Wifi P2P mode is enabled or not, alert
                    // the Activity.
                    val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        isWifiP2pEnabled = true
                    }
                }
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    // The peer list has changed! We should probably do something about
                    // that.
                    /* ————————————————
                    // api > 18 have this extra info
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        val wifiP2pList: WifiP2pDeviceList = intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST)
                        println(wifiP2pList)
                    } else { //if the sdk version lower than 18
                        //get WifiP2pDeviceList by call WifiP2pManager.requestPeers to get
                        WifiDirectManager.requestPeers()
                    }
                    ————————————————*/
                    Log.d(TAG, "P2P peers changed")
                    manager.requestPeers(channel, peersListener)

                }
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    // Connection state changed! We should probably do something about
                    // that.
                    manager.let {
                        if (isNetworkAvailable()) {
                            it.requestConnectionInfo(channel,connectionListener)
                        }
                    }
                }
                WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                    val peer =
                        intent.getParcelableExtra<WifiP2pDevice>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
                    when (peer?.status) {
                        WifiP2pDevice.CONNECTED -> {
                            // do some thing
                        }
                    }
                }
            }
        }
    }
}
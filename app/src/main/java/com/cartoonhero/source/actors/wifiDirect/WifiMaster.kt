package com.cartoonhero.source.actors.wifiDirect

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSpecifier
import android.os.Build
import com.cartoonhero.source.actormodel.Actor
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class WifiMaster(val context: Context) : Actor() {
    private val manager: WifiManager = context.applicationContext
        .getSystemService(Context.WIFI_SERVICE) as WifiManager

    private fun beCheckWifiEnabled(
        sender: Actor,complete: (Boolean) -> Unit) {
        sender.send {
            complete(manager.isWifiEnabled)
        }
    }

    private fun beScanWifi(sender: Actor, complete: (List<ScanResult>) -> Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            val scanJob: Deferred<List<ScanResult>> =
                async {
                    manager.scanResults
                }
            val result = scanJob.await()
            if (result.isNotEmpty()) {
                sender.send {
                    complete(result)
                }
            }
        }
    }

    private fun beConnect(
        sender: Actor, ssid: String, pass: String, complete: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val specifier =
                WifiNetworkSpecifier.Builder()
                    .setSsid(ssid).setWpa2Passphrase(pass).build()
            val netRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_TRUSTED)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_VPN)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_FOREGROUND)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_CONGESTED)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_NOT_ROAMING)
                .setNetworkSpecifier(specifier)
                .build();
            val connManager: ConnectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connManager.requestNetwork(
                netRequest, object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        super.onAvailable(network)
                        connManager.bindProcessToNetwork(network)
                        sender.send {
                            complete(true)
                        }
                    }
                    override fun onUnavailable() {
                        super.onUnavailable()
                        sender.send {
                            complete(false)
                        }
                    }
                })
        } else {
            TODO("VERSION.SDK_INT < Q")
        }
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeCheckWifiEnabled(
        sender: Actor,complete: (Boolean) -> Unit) {
        send {
            beCheckWifiEnabled(sender, complete)
        }
    }
    fun toBeScanWifi(sender: Actor, complete: (List<ScanResult>) -> Unit) {
        send {
            beScanWifi(sender, complete)
        }
    }
    fun toBeConnect(
        sender: Actor, ssid: String, pass: String,
        complete: (Boolean) -> Unit) {
        send {
            beConnect(sender, ssid, pass, complete)
        }
    }
}
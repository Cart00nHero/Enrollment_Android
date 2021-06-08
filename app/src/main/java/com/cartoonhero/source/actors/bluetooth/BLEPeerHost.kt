package com.cartoonhero.source.actors.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.cartoonhero.source.actormodel.Actor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class BLEPeerHost(context: Context): Actor() {
    private val mContext: Context = context
    private var serviceBoundEvent: (() -> Unit)? = null
    private val bleManager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private var gattService: BLEGattService? = null

    private fun beCheckBleEnable(complete: (Boolean) -> Unit) {
        complete(bleManager.adapter.isEnabled)
    }

    private fun beDiscoverPeers(detector: ScanCallback) {
        val scanner = bleManager.adapter.bluetoothLeScanner
        scanner.startScan(detector)
    }

    private fun beStopDiscoverPeers(detector: ScanCallback) {
        val scanner = bleManager.adapter.bluetoothLeScanner
        scanner.stopScan(detector)
    }
    private fun beConnectToGatt(
        peer: BluetoothDevice,callback: BluetoothGattCallback) {
        if (serviceBoundEvent == null) {
            val intent = Intent()
            intent.setClass(mContext, BLEGattService::class.java)
            mContext.bindService(intent,connection, Context.BIND_AUTO_CREATE)
            serviceBoundEvent = {
                gattService?.connectGatt(peer,callback)
            }
        }
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeCheckBLEnable(complete: (Boolean) -> Unit) {
        send {
            beCheckBleEnable(complete)
        }
    }
    fun toBeDiscoverPeers(detector: ScanCallback) {
        send {
            beDiscoverPeers(detector)
        }
    }
    fun toBeStopDiscoverPeers(detector: ScanCallback) {
        send {
            beStopDiscoverPeers(detector)
        }
    }
    fun toBeConnectToGatt(
        peer: BluetoothDevice,callback: BluetoothGattCallback) {
        send {
            beConnectToGatt(peer, callback)
        }
    }

    /* --------------------------------------------------------------------- */
    // MARK: - Interface implements
    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: BLEGattService.ServiceBinder =
                service as BLEGattService.ServiceBinder
            gattService = binder.gattService
            serviceBoundEvent?.let { it() }
        }
        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }
}
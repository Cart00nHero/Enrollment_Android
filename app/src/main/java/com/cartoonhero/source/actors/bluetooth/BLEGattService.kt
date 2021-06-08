package com.cartoonhero.source.actors.bluetooth

import android.app.Service
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class BLEGattService : Service() {
    private var bluetoothGatt: BluetoothGatt? = null
    override fun onBind(intent: Intent?): IBinder {
        return ServiceBinder()
    }

    inner class ServiceBinder : Binder() {
        val gattService: BLEGattService
            get() = this@BLEGattService
    }
    fun connectGatt(device: BluetoothDevice,callback: BluetoothGattCallback) {
        bluetoothGatt =
            device.connectGatt(this, false, callback)
    }

}
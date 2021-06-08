package com.cartoonhero.source.actors.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Context
import com.cartoonhero.source.actormodel.Actor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class BLEPeer(peer: BluetoothDevice): Actor() {
    private val device = peer
    private var bluetoothGatt: BluetoothGatt? = null
    private var stateSubscriber:((BluetoothDevice,Int) -> Unit)? = null

    private fun beConnect(
        context: Context,peerState:(BluetoothDevice,state: Int) -> Unit) {
        stateSubscriber = peerState
        bluetoothGatt = device.connectGatt(context,false,gattCallback)
    }

    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeConnect(
        context: Context,peerState:(BluetoothDevice,state: Int) -> Unit) {
        send {
            beConnect(context, peerState)
        }
    }

    /* --------------------------------------------------------------------- */
    // MARK: - Interface implements
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            stateSubscriber?.let { it(device,newState) }
            when(newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    bluetoothGatt?.discoverServices()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
        }
    }
}
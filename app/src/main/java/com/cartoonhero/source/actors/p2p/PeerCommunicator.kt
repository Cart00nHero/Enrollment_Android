package com.cartoonhero.source.actors.p2p

import android.util.Log
import com.cartoonhero.source.actormodel.Actor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.net.InetSocketAddress
import java.net.ServerSocket

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class PeerCommunicator: Actor() {
    private val serverSocket: ServerSocket = ServerSocket()

    private fun beRun() {
        serverSocket.reuseAddress = true
        serverSocket.bind(InetSocketAddress(8080))
        while (!Thread.interrupted()) {
            val clientSocket = serverSocket.accept()
            Log.d("Client ip address:",
                clientSocket.inetAddress.hostAddress)
        }
    }
}
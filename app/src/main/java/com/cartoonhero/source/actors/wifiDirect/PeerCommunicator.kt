package com.cartoonhero.source.actors.wifiDirect

import com.cartoonhero.source.actormodel.Actor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class PeerCommunicator: Actor() {
    private val serverSocket: ServerSocket = ServerSocket()
    private var clientSocket: Socket? = null
    private var peerWriter: PrintStream? = null
    private var peerReader: BufferedReader? = null

    private fun beRun() {
        serverSocket.reuseAddress = true
        serverSocket.bind(InetSocketAddress(8080))
        if (clientSocket == null) {
            clientSocket = serverSocket.accept()
        }
        if (peerWriter == null) {
            peerWriter = PrintStream(clientSocket?.getOutputStream())
        }
        if (peerReader == null) {
            peerReader =
                BufferedReader(
                    InputStreamReader(clientSocket?.getInputStream())
                )
        }
    }
    private fun beWriteMessage(message: String) {
        peerWriter?.print(message)
    }
    private fun beReadMessage(
        sender: Actor,subscriber:(String) -> Unit) {
        while (peerReader != null) {
            val message: String = peerReader!!.readLine()
            sender.send {
                subscriber(message)
            }
        }
    }
    private fun beStop() {
        peerWriter?.close()
        peerReader?.close()
        clientSocket?.close()
        serverSocket.close()
        peerWriter = null
        peerReader = null
        clientSocket = null
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeRun() {
        send {
            beRun()
        }
    }
    fun toBeWriteMessage(message: String) {
        send {
            beWriteMessage(message)
        }
    }
    fun toBeReadMessage(
        sender: Actor,subscriber:(String) -> Unit) {
        send {
            beReadMessage(sender, subscriber)
        }
    }

    fun toBeStop() {
        send {
            beStop()
        }
    }
}
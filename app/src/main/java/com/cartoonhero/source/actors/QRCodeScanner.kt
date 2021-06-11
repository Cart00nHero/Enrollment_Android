package com.cartoonhero.source.actors

import android.util.Log
import com.cartoonhero.source.actormodel.Actor
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class QRCodeScanner : Actor() {
    private fun beDecode(
        sender: Actor,image: InputImage,
        complete:(List<Barcode>) -> Unit) {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_AZTEC
            )
            .build()
        val scanner = BarcodeScanning.getClient(options)
        scanner.process(image).addOnSuccessListener { barcodes ->
            sender.send {
                complete(barcodes)
            }
        }
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeDecode(
        sender: Actor,image: InputImage,
        complete:(List<Barcode>) -> Unit) {
        send {
            beDecode(sender, image, complete)
        }
    }
}
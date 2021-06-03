package com.cartoonhero.source.enrollment_android.scene.qrCode

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.actors.QRCodeScanner
import com.cartoonhero.source.actors.ToolMan
import com.cartoonhero.source.actors.UISize
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class QRCodeScenario: Actor() {
    private fun beScanQrCodeImage(qrBitmap: Bitmap) {
        val qrImage = InputImage.fromBitmap(qrBitmap,0)
        QRCodeScanner().toBeDecode(qrImage)
    }
    private fun bePrePareQRCodeMessage(qrBitmap: Bitmap) {
        val newSize = UISize(
            width = 200,
            height = 200
        )
        ToolMan().toBeResizeBitmap(this,qrBitmap,newSize) { newBitmap ->
            ToolMan().toBeBitmapToBase64(this,newBitmap) {
                // do something
                Log.d("拿到圖",it)
            }
        }
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeScanQrCodeImage(qrBitmap: Bitmap) {
        send {
            beScanQrCodeImage(qrBitmap)
        }
    }
}
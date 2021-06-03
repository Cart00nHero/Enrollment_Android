package com.cartoonhero.source.enrollment_android.scene.qrCode

import android.graphics.Bitmap
import android.util.Log
import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.actors.QRCodeScanner
import com.cartoonhero.source.actors.ToolMan
import com.cartoonhero.source.actors.UISize
import com.cartoonhero.source.actors.express.Courier
import com.cartoonhero.source.actors.express.Parcel
import com.cartoonhero.source.props.inlineMethods.toBitmap
import com.cartoonhero.source.redux.ReduxFactory
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class QRCodeScenario: Actor() {
    private fun beCollectBitmap(complete:(Bitmap) -> Unit) {
        Courier().toBeClaim(this) {
            for (parcel in it) {
                if (parcel.content is String) {
                    ToolMan().toBeBase64ToBitMap(this,parcel.content) { qrBitmap ->
                        CoroutineScope(Dispatchers.Main).launch {
                            complete(qrBitmap)
                        }
                    }
                }
            }
        }
    }
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
            }
        }
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeCollectBitmap(complete:(Bitmap) -> Unit) {
        send {
            beCollectBitmap(complete)
        }
    }
    fun toBeScanQrCodeImage(qrBitmap: Bitmap) {
        send {
            beScanQrCodeImage(qrBitmap)
        }
    }
    fun tobePrePareQRCodeMessage(qrBitmap: Bitmap) {
        send {
            bePrePareQRCodeMessage(qrBitmap)
        }
    }
}
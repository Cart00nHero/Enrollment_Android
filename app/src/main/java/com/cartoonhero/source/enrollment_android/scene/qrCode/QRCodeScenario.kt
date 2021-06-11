package com.cartoonhero.source.enrollment_android.scene.qrCode

import android.graphics.Bitmap
import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.actors.DataConverter
import com.cartoonhero.source.actors.QRCodeScanner
import com.cartoonhero.source.actors.ToolMan
import com.cartoonhero.source.actors.UISize
import com.cartoonhero.source.actors.express.Courier
import com.cartoonhero.source.props.entities.VisitedUnit
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class QRCodeScenario : Actor() {
    private var unitInfo: VisitedUnit? = null
    private fun beCollectBitmap(complete: (Bitmap) -> Unit) {
        Courier().toBeClaim(this) {
            for (parcel in it) {
                if (parcel.content is VisitedUnit) {
                    unitInfo = parcel.content
                    if (unitInfo != null) {
                        DataConverter().toBeBase64ToBitMap(
                            this, unitInfo!!.qrB64Image) { image ->
                            CoroutineScope(Dispatchers.Main).launch {
                                complete(image)
                            }
                            val qrImage = InputImage.fromBitmap(image, 0)
                            QRCodeScanner().toBeDecode(this, qrImage) { barcodes ->
                                for (barcode in barcodes) {
                                    val bounds = barcode.boundingBox
                                    val corners = barcode.cornerPoints
                                    val rawValue = barcode.rawValue
                                    // See API reference for complete list of supported types
                                    when (barcode.valueType) {
                                        Barcode.TYPE_WIFI -> {
                                            val ssid = barcode.wifi!!.ssid
                                            val password = barcode.wifi!!.password
                                            val type = barcode.wifi!!.encryptionType
                                        }
                                        Barcode.TYPE_URL -> {
                                            val title = barcode.url!!.title
                                            val url = barcode.url!!.url
                                            Courier().toBeApplyExpress(
                                                this,
                                                "WebViewScenario",url,null)
                                        }
                                        Barcode.TYPE_SMS -> {
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun bePrePareQRCodeMessage(qrBitmap: Bitmap) {
        val newSize = UISize(
            width = 200,
            height = 200
        )
        ToolMan().toBeResizeBitmap(this, qrBitmap, newSize) { newBitmap ->
            DataConverter().toBeBitmapToBase64(this, newBitmap) {
                // do something
            }
        }
    }

    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeCollectBitmap(complete: (Bitmap) -> Unit) {
        send {
            beCollectBitmap(complete)
        }
    }

    fun tobePrePareQRCodeMessage(qrBitmap: Bitmap) {
        send {
            bePrePareQRCodeMessage(qrBitmap)
        }
    }
}
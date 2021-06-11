package com.cartoonhero.source.enrollment_android.scene.qrCode

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.actors.DataConverter
import com.cartoonhero.source.actors.QRCodeScanner
import com.cartoonhero.source.actors.ToolMan
import com.cartoonhero.source.actors.UISize
import com.cartoonhero.source.actors.express.Courier
import com.cartoonhero.source.props.entities.VisitedUnit
import com.cartoonhero.source.redux.actions.GetQrCodeAction
import com.cartoonhero.source.redux.appStore
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.*
import java.net.URI

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class QRCodeScenario : Actor() {
    private var unitInfo: VisitedUnit = VisitedUnit()
    private var smsSubscriber: ((String, String) -> Unit)? = null
    private fun beCollectBitmap(complete: (Bitmap) -> Unit) {
        Courier().toBeClaim(this) { pSet ->
            for (parcel in pSet) {
                if (parcel.content is VisitedUnit) {
                    unitInfo = parcel.content
                    DataConverter().toBeBase64ToBitMap(
                        this, unitInfo.qrB64Image) { image ->
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
                                        val number = barcode.sms?.phoneNumber
                                        val message = barcode.sms?.message ?: ""
                                        if (number != null) {
                                            smsSubscriber?.let { it(number,message) }
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

    private fun bePrePareQRCodeMessage(context:Context, imagePath: Uri) {
        val bitmap: Bitmap = BitmapFactory.decodeStream(
            context.contentResolver.openInputStream(imagePath))
        val newSize = UISize(
            width = 200,
            height = 200
        )
        ToolMan().toBeResizeBitmap(this, bitmap, newSize) { newBitmap ->
            DataConverter().toBeBitmapToBase64(this, newBitmap) {
                unitInfo.qrB64Image = it
                appStore.dispatch(GetQrCodeAction(context,it))
            }
        }
    }
    private fun beSubscribeSMS(
        subscriber: (phone: String,message: String) -> Unit) {
        smsSubscriber = subscriber
    }

    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeCollectBitmap(complete: (Bitmap) -> Unit) {
        send {
            beCollectBitmap(complete)
        }
    }
    fun toBePrePareQRCodeMessage(context:Context, imagePath: Uri) {
        send {
            bePrePareQRCodeMessage(context, imagePath)
        }
    }
    fun toBeSubscribeSMS(
        subscriber: (phone: String,message: String) -> Unit) {
        send {
            beSubscribeSMS(subscriber)
        }
    }
}
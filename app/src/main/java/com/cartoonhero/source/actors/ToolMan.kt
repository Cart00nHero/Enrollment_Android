package com.cartoonhero.source.actors

import android.graphics.Bitmap
import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.props.inlineMethods.toBase64String
import com.cartoonhero.source.props.inlineMethods.toBitmap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

data class UISize(
    val width: Int,
    val height: Int
)

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class ToolMan : Actor() {
    private fun beResizeBitmap(
        sender: Actor, bitmap: Bitmap, targetSize: UISize,
        complete: (Bitmap) -> Unit) {
        val oldSize = UISize(
            width = bitmap.width,
            height = bitmap.height
        )
        val widthRatio: Float = (targetSize.width).toFloat() / (oldSize.width).toFloat()
        val heightRatio = (targetSize.height).toFloat() / (oldSize.height).toFloat()
        val newSize: UISize =
            if (widthRatio > heightRatio) {
                UISize(
                    ((oldSize.width * heightRatio).toInt()),
                    ((oldSize.height * heightRatio).toInt())
                )
            } else {
                UISize(
                    ((oldSize.width * widthRatio).toInt()),
                    ((oldSize.height * widthRatio).toInt())
                )
            }
        val newBitmap = Bitmap.createScaledBitmap(
            bitmap,
            newSize.width,
            newSize.height,
            false
        )
        sender.send {
            complete(newBitmap)
        }
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeResizeBitmap(
        sender: Actor, bitmap: Bitmap, targetSize: UISize,
        complete: (Bitmap) -> Unit) {
        send {
            beResizeBitmap(sender, bitmap, targetSize, complete)
        }
    }
}
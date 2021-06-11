package com.cartoonhero.source.actors

import android.graphics.Bitmap
import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.props.entities.VisitedUnit
import com.cartoonhero.source.props.inlineMethods.convertAnyToJson
import com.cartoonhero.source.props.inlineMethods.toEntity
import com.cartoonhero.source.props.inlineMethods.toBase64String
import com.cartoonhero.source.props.inlineMethods.toBitmap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class DataConverter : Actor() {
    private fun beBitmapToBase64(
        sender: Actor, bitmap: Bitmap, complete: (String) -> Unit) {
        val base64Bitmap: String = bitmap.toBase64String()
        sender.send {
            complete(base64Bitmap)
        }
    }

    private fun beBase64ToBitMap(
        sender: Actor, base64Img: String,
        complete: (Bitmap) -> Unit) {
        val bitmap = base64Img.toBitmap()
        sender.send {
            bitmap?.let { complete(it) }
        }
    }

    private fun beJsonToVisitedUnit(
        sender: Actor, json: String, complete: (VisitedUnit) -> Unit) {
        if (json.isNotEmpty()) {
            val vUnit: VisitedUnit =
                json.toEntity<VisitedUnit>() ?: VisitedUnit()
            sender.send {
                complete(vUnit)
            }
        }
    }

    private fun beVisitedUnitToJson(
        sender: Actor, unit: VisitedUnit, complete: (String) -> Unit) {
        val json: String = convertAnyToJson(unit)
        sender.send {
            complete(json)
        }
    }

    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeBitmapToBase64(
        sender: Actor, bitmap: Bitmap, complete: (String) -> Unit) {
        send {
            beBitmapToBase64(sender, bitmap, complete)
        }
    }

    fun toBeBase64ToBitMap(
        sender: Actor, base64Img: String,
        complete: (Bitmap) -> Unit) {
        send {
            beBase64ToBitMap(sender, base64Img, complete)
        }
    }

    fun toBeJsonToVisitedUnit(
        sender: Actor, json: String, complete: (VisitedUnit) -> Unit) {
        if (json.isNotEmpty()) {
            val vUnit: VisitedUnit =
                json.toEntity<VisitedUnit>() ?: VisitedUnit()
            sender.send {
                complete(vUnit)
            }
        }
    }

    fun toBeVisitedUnitToJson(
        sender: Actor, unit: VisitedUnit, complete: (String) -> Unit) {
        val json: String = convertAnyToJson(unit)
        sender.send {
            complete(json)
        }
    }
}
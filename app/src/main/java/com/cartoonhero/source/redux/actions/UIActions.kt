package com.cartoonhero.source.redux.actions

import android.content.Context
import com.cartoonhero.source.enrollment_android.scenery.WifiScanItemView
import org.rekotlin.Action

class InputValueChangedAction(
    val tag: Int,val value: String):Action

class GetQrCodeAction(
    val context: Context, val b64Image: String):Action
class WifiConnectBtnClickAction(val itemView: WifiScanItemView,
    val ssidName: String,val pass:String):Action
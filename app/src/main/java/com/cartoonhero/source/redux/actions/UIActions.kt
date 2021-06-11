package com.cartoonhero.source.redux.actions

import android.content.Context
import org.rekotlin.Action

class InputValueChangedAction(
    val tag: Int,val value: String):Action

class GetQrCodeAction(
    val context: Context, val b64Image: String):Action
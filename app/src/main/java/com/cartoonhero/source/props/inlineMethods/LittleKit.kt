package com.cartoonhero.source.props.inlineMethods

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

inline fun SharedPreferences.applyEdit(
    func: SharedPreferences.Editor.() -> SharedPreferences.Editor) {
    this.edit().func().apply()
}
fun Bitmap.toBase64String():String{
    ByteArrayOutputStream().apply {
        compress(Bitmap.CompressFormat.PNG,10,this)
        return Base64.encodeToString(toByteArray(),Base64.DEFAULT)
    }
}
fun String.toBitmap():Bitmap?{
    Base64.decode(this,Base64.DEFAULT).apply {
        return BitmapFactory.decodeByteArray(this,0,size)
    }
}
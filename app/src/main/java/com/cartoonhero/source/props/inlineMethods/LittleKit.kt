package com.cartoonhero.source.props.inlineMethods

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.InputFilter
import android.util.Base64
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

inline fun SharedPreferences.applyEdit(
    func: SharedPreferences.Editor.() -> SharedPreferences.Editor
) {
    this.edit().func().apply()
}

fun Bitmap.toBase64String(): String {
    ByteArrayOutputStream().apply {
        compress(Bitmap.CompressFormat.PNG, 10, this)
        return Base64.encodeToString(toByteArray(), Base64.DEFAULT)
    }
}

fun String.toBitmap(): Bitmap? {
    Base64.decode(this, Base64.DEFAULT).apply {
        return BitmapFactory.decodeByteArray(this, 0, size)
    }
}

fun View.showKeyboard(context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun EditText.setMaxLength(maxLength: Int) {
    filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
}
@Suppress("BlockingMethodInNonBlockingContext")
fun URL.isReachable(complete:(Boolean) -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        val requestJob:Deferred<Boolean> = async {
            with(this@isReachable.openConnection() as HttpURLConnection) {
                return@async responseCode == 200
            }
        }
        withContext(Dispatchers.Default) {
            val result = requestJob.await()
            complete(result)
        }
    }
}
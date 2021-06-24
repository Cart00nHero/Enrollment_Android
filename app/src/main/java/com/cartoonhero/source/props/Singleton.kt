package com.cartoonhero.source.props

import android.annotation.SuppressLint
import android.content.Context

const val sharedPreferencesName = "enrollment_android_sharedPreferences"
class Singleton {
    var currentRole: String = ""
    var mainContext:Context? = null
    companion object {
        @SuppressLint("StaticFieldLeak")
        val instance = Singleton()
        const val sharePrefsKey = "Enrollment_SharedPreferences_Key"
        const val clipLabel = "Clip_Data_Label"
    }
}

fun localized(resId: Int): String {
    return Singleton.instance.mainContext?.getString(resId)?:""
}
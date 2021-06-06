package com.cartoonhero.source.props

import android.content.Context

const val sharedPreferencesName = "enrollment_android_sharedPreferences"
class Singleton {
    var currentRole: String = ""
    companion object {
        val instance = Singleton()
        const val sharePrefsKey = "Enrollment_SharedPreferences_Key"
        const val clipLabel = "Clip_Data_Label"
    }
}

fun localized(context: Context,resId: Int): String {
    return context.getString(resId)
}
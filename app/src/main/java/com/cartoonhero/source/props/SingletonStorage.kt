package com.cartoonhero.source.props

const val sharedPreferencesName = "enrollment_android_sharedPreferences"
class SingletonStorage {
    companion object {
        val instance = SingletonStorage()
    }
}
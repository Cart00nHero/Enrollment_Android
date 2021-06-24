package com.cartoonhero.source.props

import androidx.fragment.app.Fragment
import com.cartoonhero.source.enrollment_android.scene.opening.OpenningFragment
import com.cartoonhero.source.enrollment_android.scene.wifiScan.WifiScanFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
fun openingFragment(name: String): Fragment {
    return when(name) {
        "WifiScanFragment" -> WifiScanFragment()
        else -> OpenningFragment()
    }
}
fun instanceFrom(name: String): Any? {
    val clz: Class<*> = Class.forName(name)
    return clz.kotlin.objectInstance
}
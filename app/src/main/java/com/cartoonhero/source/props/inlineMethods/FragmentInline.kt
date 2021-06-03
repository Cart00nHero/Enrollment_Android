package com.cartoonhero.source.props.inlineMethods

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

fun FragmentManager.inTransaction(func: FragmentTransaction.()-> FragmentTransaction) {
//    beginTransaction().func().commit()
    beginTransaction().func().commitAllowingStateLoss()
}
fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction {
        add(frameId,fragment)
    }
}
fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.inTransaction {
        replace(frameId,fragment)
    }
}
fun AppCompatActivity.removeFragment(fragment: Fragment) {
    supportFragmentManager.inTransaction {
        remove(fragment)
    }
}
fun AppCompatActivity.findFragment(containerId: Int): Fragment? {
    return supportFragmentManager.findFragmentById(containerId)
}
internal inline fun <reified T : Activity> Activity.startActivity(inInitializer: Intent.() -> Unit) {
    startActivity(
        Intent(this,T::class.java).apply(inInitializer)
    )
}
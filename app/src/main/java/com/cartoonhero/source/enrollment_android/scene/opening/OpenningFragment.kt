package com.cartoonhero.source.enrollment_android.scene.opening

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cartoonhero.source.enrollment_android.MainActivity
import com.cartoonhero.source.enrollment_android.StageResId
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.enrollment_android.scene.roleSelection.RoleSelectionFragment
import com.cartoonhero.source.enrollment_android.scene.tabMenu.TabMenuFragment
import com.cartoonhero.source.props.Singleton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

class OpenningFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(
            R.layout.fragment_openning,container,false)
    }

    @ExperimentalCoroutinesApi
    @ObsoleteCoroutinesApi
    override fun onStart() {
        super.onStart()
        val sharePrefs = context?.getSharedPreferences(
            Singleton.sharePrefsKey, Context.MODE_PRIVATE)
        Singleton.instance.currentRole =
            sharePrefs?.getString("role_of_user","") ?: ""
        when(Singleton.instance.currentRole) {
            "" -> {
                (activity as MainActivity).goForward(
                    listOf(RoleSelectionFragment()),StageResId)
            }
            else -> {
                (activity as MainActivity).goForward(
                    listOf(TabMenuFragment()),StageResId)
            }
        }
    }
}
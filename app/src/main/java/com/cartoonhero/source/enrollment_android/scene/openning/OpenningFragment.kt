package com.cartoonhero.source.enrollment_android.scene.openning

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cartoonhero.source.enrollment_android.MainActivity
import com.cartoonhero.source.enrollment_android.MainFragmentContainer
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.enrollment_android.scene.roleSelection.RoleSelectionFragment
import com.cartoonhero.source.enrollment_android.scene.visitedUnit.UnitFragment
import com.cartoonhero.source.enrollment_android.scene.visitor.VisitorFragment
import com.cartoonhero.source.props.SingletonStorage
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
            SingletonStorage.sharePrefsKey, Context.MODE_PRIVATE)
        when(sharePrefs?.getString("role_of_user","")) {
            "" -> {
                (activity as MainActivity).goForward(
                    listOf(RoleSelectionFragment()),MainFragmentContainer)
            }
            "Visitor" -> {
                (activity as MainActivity).goForward(
                    listOf(VisitorFragment()),MainFragmentContainer)
            }
            "Visited_Unit" -> {
                (activity as MainActivity).goForward(
                    listOf(UnitFragment()),MainFragmentContainer)
            }
        }
    }
}
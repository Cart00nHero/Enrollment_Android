package com.cartoonhero.source.enrollment_android.scene.visitor

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.props.SingletonStorage
import com.cartoonhero.source.props.inlineMethods.applyEdit
import kotlinx.android.synthetic.main.fragment_visitor.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class VisitorFragment: Fragment() {
    private val scenario = VisitorScenario()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_visitor,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scenario.toBeCheckPermission(requireContext()) { granted ->
            if (!granted) {
                activity?.let { scenario.toBeRequestPermission(it) }
            }
        }
        button_change_role.setOnClickListener {
            val sharePrefs = context?.getSharedPreferences(
                SingletonStorage.sharePrefsKey,Context.MODE_PRIVATE)
            sharePrefs?.applyEdit {
                remove("role_of_user")
            }
        }
    }
}
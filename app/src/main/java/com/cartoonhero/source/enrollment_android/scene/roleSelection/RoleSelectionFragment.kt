package com.cartoonhero.source.enrollment_android.scene.roleSelection

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cartoonhero.source.enrollment_android.MainActivity
import com.cartoonhero.source.enrollment_android.MainFragmentContainer
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.props.Singleton
import com.cartoonhero.source.props.inlineMethods.applyEdit
import kotlinx.android.synthetic.main.fragment_role_selection.*

class RoleSelectionFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(
            R.layout.fragment_role_selection,
            container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_visitor.setOnClickListener {
            val sharePrefs = context?.getSharedPreferences(
                Singleton.sharePrefsKey,Context.MODE_PRIVATE)
            sharePrefs?.applyEdit {
                putString("role_of_user","Visitor")
            }
            (activity as MainActivity).goBack(MainFragmentContainer)
        }
        button_unit.setOnClickListener {
            val sharePrefs = context?.getSharedPreferences(
                Singleton.sharePrefsKey,Context.MODE_PRIVATE)
            sharePrefs?.applyEdit {
                putString("role_of_user","Visited_Unit")
            }
            (activity as MainActivity).goBack(MainFragmentContainer)
        }
    }
}
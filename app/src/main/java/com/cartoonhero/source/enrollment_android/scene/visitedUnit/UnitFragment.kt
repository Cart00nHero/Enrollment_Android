package com.cartoonhero.source.enrollment_android.scene.visitedUnit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.enrollment_android.scenery.EditItemView
import com.cartoonhero.source.props.Singleton
import com.cartoonhero.source.props.entities.ListEditItem
import com.cartoonhero.source.props.inlineMethods.applyEdit
import kotlinx.android.synthetic.main.fragment_visitor.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class UnitFragment: Fragment() {
    private val scenario = UnitScenario()
    private var dataSource:List<ListEditItem> = listOf()
    private var isEditState = false
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_unit,container,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_change_role.setOnClickListener {
            val sharePrefs = context?.getSharedPreferences(
                Singleton.sharePrefsKey, Context.MODE_PRIVATE)
            sharePrefs?.applyEdit {
                remove("role_of_user")
            }
        }
    }

    private inner class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview_edit, parent, false)
            return ViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return dataSource.size
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemView.layoutParams.height =
                (editor_list.measuredHeight/3)
            holder.itemView.tag = position
            holder.bindData(
                holder.itemView as EditItemView<*>,position)
        }
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bindData(itemView: EditItemView<*>, position: Int) {
                val data = dataSource[position]
                itemView.bindItemData(data,isEditState)
            }
        }
    }
}
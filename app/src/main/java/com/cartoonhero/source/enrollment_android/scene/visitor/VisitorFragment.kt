package com.cartoonhero.source.enrollment_android.scene.visitor

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.enrollment_android.scenery.EditItemView
import com.cartoonhero.source.props.Singleton
import com.cartoonhero.source.props.entities.ListEditItem
import com.cartoonhero.source.props.inlineMethods.applyEdit
import com.cartoonhero.source.props.inlineMethods.hideKeyboard
import com.cartoonhero.source.props.inlineMethods.setMaxLength
import com.cartoonhero.source.props.localized
import kotlinx.android.synthetic.main.fragment_visitor.*
import kotlinx.android.synthetic.main.layout_text_field.view.*
import kotlinx.coroutines.*

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class VisitorFragment: Fragment() {
    private val scenario = VisitorScenario()
    private var editDataSource:List<ListEditItem> = listOf()
    private var isEditState = false
    private val concatAdapter =
        ConcatAdapter(RecyclerAdapter(),RecyclerAdapter())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(
            R.layout.fragment_visitor,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            scenario.toBeGetDataSource(it) { source ->
                editDataSource = source
                CoroutineScope(Dispatchers.Main).launch {
                    this@VisitorFragment.editor_list.adapter =
                        concatAdapter.adapters[0]
                }
            }
            scenario.toBeCheckPermission(it) { granted ->
                activity?.let { activity ->
                    if (granted) {
                        scenario.toBeBuildP2PConnection(activity) {
                        }
                    } else {
                        scenario.toBeRequestPermission(activity) }
                    }
                }
            }
        this.button_edit.setOnClickListener { btn ->
            when((btn as Button).text) {
                localized(requireContext(),R.string.edit) -> {
                    isEditState = true
                    concatAdapter.adapters[0].notifyDataSetChanged()
                    btn.text =
                        localized(requireContext(),R.string.save)
                }
                localized(requireContext(),R.string.save) -> {
                    isEditState = false
                    context?.let { scenario.toBeSaveVisitor(it) }
                    btn.text = localized(requireContext(),R.string.edit)
                    btn.hideKeyboard()
                }
            }
        }
        button_change_role.setOnClickListener {
            val sharePrefs = context?.getSharedPreferences(
                Singleton.sharePrefsKey,Context.MODE_PRIVATE)
            sharePrefs?.applyEdit {
                remove("role_of_user")
            }
        }
    }
    override fun onStart() {
        super.onStart()
        scenario.toBeSubscribeRedux {
        }
    }

    override fun onStop() {
        super.onStop()
        scenario.toBeUnSubscribeRedux()
    }

    override fun onDestroy() {
        super.onDestroy()
        scenario.toBeDestroyP2P()
    }
    private inner class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview_edit, parent, false)
            return ViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return editDataSource.size
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemView.layoutParams.height =
                (editor_list.measuredHeight/3)
            holder.itemView.tag = position
            holder.bindData(
                holder.itemView as EditItemView,position)
        }
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bindData(itemView: EditItemView, position: Int) {
                val data = editDataSource[position]
                itemView.text_field.setMaxLength(25)
                itemView.bindItemData(data,isEditState)
            }
        }
    }
}
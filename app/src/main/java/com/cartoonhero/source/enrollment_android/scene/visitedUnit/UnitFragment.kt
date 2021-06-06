package com.cartoonhero.source.enrollment_android.scene.visitedUnit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.enrollment_android.scene.visitor.VisitorScenario
import com.cartoonhero.source.enrollment_android.scenery.EditItemView
import com.cartoonhero.source.props.Singleton
import com.cartoonhero.source.props.TabLayoutItem
import com.cartoonhero.source.props.entities.ListEditItem
import com.cartoonhero.source.props.inlineMethods.applyEdit
import com.cartoonhero.source.props.inlineMethods.hideKeyboard
import com.cartoonhero.source.props.inlineMethods.setMaxLength
import com.cartoonhero.source.props.localized
import com.cartoonhero.source.props.toDp
import kotlinx.android.synthetic.main.fragment_visitor.*
import kotlinx.android.synthetic.main.layout_text_field.view.*
import kotlinx.coroutines.*

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class UnitFragment : Fragment() {

    private val scenario = UnitScenario()
    private var dataSource: List<ListEditItem> = listOf()
    private var isEditState = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_unit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpConnection()
        context?.let {
            scenario.toBeRoleChanged(it) { title ->
                this.button_change_role.text = title
            }
            VisitorScenario().toBeCheckPermission(it) { granted ->
                activity?.let { act ->
                    if (!granted)
                        scenario.toBeRequestPermission(act)
                }
            }
        }
        this.button_edit.setOnClickListener { btn ->
            when ((btn as Button).text) {
                localized(requireContext(), R.string.edit) -> {
                    isEditState = true
                    this.editor_list.adapter?.notifyDataSetChanged()
                    btn.text =
                        localized(requireContext(), R.string.save)
                }
                localized(requireContext(), R.string.save) -> {
                    isEditState = false
                    context?.let { scenario.toBeSaveUnitInfo(it) }
                    btn.text = localized(requireContext(), R.string.edit)
                    btn.hideKeyboard()
                }
            }
        }
        this.button_change_role.setOnClickListener { clickView ->
            context?.let {
                if ((clickView as Button).text !=
                    localized(it,R.string.role_changed))
                    scenario.toBeSwitchRole(it)
            }
        }
        this.toggleButton.setOnCheckedChangeListener { checkBtn, isChecked ->
            if (isChecked) {
                context?.let {
                    scenario.toBeSetUpConnection(it) { success ->
                        if (!success) checkBtn.isChecked = false
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        scenario.toBeSubscribeRedux {
            print("received action")
        }
    }

    override fun onStop() {
        super.onStop()
        scenario.toBeUnSubscribeRedux()
    }

    private fun setUpConnection() {
        context?.let {
            editor_list.layoutParams.height = 3 * 64.toDp(it)
            scenario.toBeSubscribeSource(it) { source ->
                dataSource = source
                CoroutineScope(Dispatchers.Main).launch {
                    this@UnitFragment.editor_list.adapter =
                        RecyclerAdapter()
                }
            }
            scenario.toBeCheckPermission(it) { granted ->
                if (!granted)
                    activity?.let { act -> scenario.toBeRequestPermission(act) }
            }
        }
    }

    private inner class RecyclerAdapter :
        RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
        ): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview_edit, parent, false)
            return ViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return dataSource.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemView.layoutParams.height =
                (editor_list.measuredHeight / 3)
            holder.itemView.tag = position
            holder.bindData(
                holder.itemView as EditItemView, position
            )
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bindData(itemView: EditItemView, position: Int) {
                val data = dataSource[position]
                itemView.text_field.setMaxLength(40)
                itemView.disableCopyButton()
                itemView.bindItemData(data, isEditState)
            }
        }
    }
}
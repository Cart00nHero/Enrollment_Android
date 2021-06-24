package com.cartoonhero.source.enrollment_android.scene.wifiScan

import android.net.wifi.ScanResult
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.enrollment_android.scenery.WifiScanItemView
import com.cartoonhero.source.redux.actions.WifiConnectBtnClickAction
import kotlinx.android.synthetic.main.fragment_wifi_scan.*
import kotlinx.coroutines.*

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
class WifiScanFragment: Fragment() {
    private val scenario = WifiScanScenario()
    private val wifiListSource:MutableList<ScanResult> = mutableListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(
            R.layout.fragment_wifi_scan,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.wifi_list.adapter = RecyclerAdapter()
        activity?.let { act->
            scenario.toBeEnableWifi(act)
        }
    }

    override fun onStart() {
        super.onStart()
        scenario.toBeSubscribeRedux { action ->
            when(action) {
                is WifiConnectBtnClickAction -> {
                    val position: Int = action.itemView.tag as Int
                    CoroutineScope(Dispatchers.Main).launch {
                        this@WifiScanFragment.wifi_list
                            .adapter?.notifyItemChanged(position)
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        scenario.toBeUnSubscribeRedux()
    }
    private inner class RecyclerAdapter:
        RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.itemview_edit, parent, false)
            return ViewHolder(itemView)
        }
        override fun getItemCount(): Int {
            return wifiListSource.size
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.itemView.tag = position
            val itemView:WifiScanItemView =
                holder.itemView as WifiScanItemView
            itemView.initialized()
            holder.bindData(itemView,position)
        }
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bindData(itemView: WifiScanItemView, position: Int) {
                val itemData = wifiListSource[position]
                itemView.bindScanResult(itemData)
            }
        }
    }
}
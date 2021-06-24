package com.cartoonhero.source.enrollment_android.scene.wifiScan

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.actors.express.Courier
import com.cartoonhero.source.actors.wifiDirect.WifiMaster
import com.cartoonhero.source.redux.ReduxFactory
import com.cartoonhero.source.redux.SceneState
import com.cartoonhero.source.redux.SceneSubscriber
import com.cartoonhero.source.redux.actions.WifiConnectBtnClickAction
import kotlinx.coroutines.*
import org.rekotlin.Action

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class WifiScanScenario : Actor() {
    private var wifiMaster: WifiMaster? = null
    private val redux = ReduxFactory()
    private var reActionSubscriber: ((Action) -> Unit)? = null
    private fun beEnableWifi(activity: Activity) {
        wifiMaster = WifiMaster(activity)
        CoroutineScope(Dispatchers.Main).launch {
            val panelIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
            } else {
                TODO("VERSION.SDK_INT < Q")
            }
            activity.startActivityForResult(panelIntent, 0)
        }
    }

    private fun beSubscribeRedux(subscriber:(Action) -> Unit) {
        reActionSubscriber = subscriber
        redux.subscribeRedux(stateSubscriber)
    }

    private fun beUnSubscribeRedux() {
        redux.unSubscribe()
    }

    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeEnableWifi(activity: Activity) {
        send {
            beEnableWifi(activity)
        }
    }

    fun toBeSubscribeRedux(subscriber:(Action) -> Unit) {
        send {
            beSubscribeRedux(subscriber)
        }
    }

    fun toBeUnSubscribeRedux() {
        send {
            beUnSubscribeRedux()
        }
    }

    /* --------------------------------------------------------------------- */
    // MARK: - Interface
    private val stateSubscriber = object : SceneSubscriber {
        override fun onNewState(state: SceneState) {
            send {
                when(state.currentAction) {
                    is WifiConnectBtnClickAction -> {
                        val action =
                            state.currentAction as WifiConnectBtnClickAction
                        wifiMaster?.toBeConnect(
                            this@WifiScanScenario,
                            action.ssidName,action.pass) { connected ->
                            action.itemView.wifiConnected = connected
                            reActionSubscriber?.let { it(action) }
                            Courier().toBeApplyExpress(
                                this@WifiScanScenario,
                                "VisitorScenario",action.ssidName,null)
                        }
                    }
                }
            }
        }
    }
}
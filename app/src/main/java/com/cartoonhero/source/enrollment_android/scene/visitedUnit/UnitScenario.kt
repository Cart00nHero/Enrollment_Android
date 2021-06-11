package com.cartoonhero.source.enrollment_android.scene.visitedUnit

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.text.InputType
import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.actors.Conservator
import com.cartoonhero.source.actors.DataConverter
import com.cartoonhero.source.actors.wifiDirect.PeerCommunicator
import com.cartoonhero.source.actors.wifiDirect.PeerConnector
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.props.Singleton
import com.cartoonhero.source.props.entities.ListEditItem
import com.cartoonhero.source.props.entities.VisitedUnit
import com.cartoonhero.source.props.inlineMethods.*
import com.cartoonhero.source.props.localized
import com.cartoonhero.source.redux.ReduxFactory
import com.cartoonhero.source.redux.SceneState
import com.cartoonhero.source.redux.SceneSubscriber
import com.cartoonhero.source.redux.actions.GetQrCodeAction
import com.cartoonhero.source.redux.actions.InputValueChangedAction
import kotlinx.coroutines.*
import org.rekotlin.Action

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class UnitScenario : Actor() {

    private var connector: PeerConnector? = null
    private val communicator = PeerCommunicator()
    private var unitInfo = VisitedUnit()
    private var sourceSubscriber: ((List<ListEditItem>) -> Unit)? = null
    private val redux = ReduxFactory()
    private var reduxStateEvent: ((Action) -> Unit)? = null
    private var roleSubscriber: ((String) -> Unit)? = null

    private fun beSubscribeRedux(
        subscriber: (Action) -> Unit) {
        reduxStateEvent = subscriber
        redux.subscribeRedux(stateSubscriber)
    }

    private fun beUnSubscribeRedux() {
        redux.unSubscribe()
    }

    private fun beSubscribeSource(
        context: Context,
        subscriber: (List<ListEditItem>) -> Unit) {
        sourceSubscriber = subscriber
        val sharePrefs =
            context.getSharedPreferences(Singleton.sharePrefsKey, Context.MODE_PRIVATE)
        val json: String = sharePrefs.getString("visited_unit_info", "") ?: ""
        if (json.isNotEmpty()) {
            unitInfo = json.toEntity<VisitedUnit>() ?: VisitedUnit()
        }
        convertSource(context)
    }

    private fun beCheckPermission(
        context: Context, complete: (Boolean) -> Unit) {
        Conservator().toBeCheckPermission(
            this,
            context, Manifest.permission.ACCESS_FINE_LOCATION) {
            CoroutineScope(Dispatchers.Main).launch {
                complete(it)
            }
        }
    }

    private fun beRequestPermission(activity: Activity) {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        Conservator().toBeRequestPermission(activity, permission)
    }

    private fun beEnableWifiDirect(
        context: Context, complete: (Boolean) -> Unit) {
        connector = PeerConnector(context)
        connector?.toBeSetup(this) {
            if (it) {
                connector?.toBeCreateGroup(this) { success ->
                    CoroutineScope(Dispatchers.Main).launch {
                        complete(success)
                    }
                }
            }
        }
    }
    private fun beDiscoverPeers() {
        connector?.toBeDiscovering(this) {
            for (peer in it) {
                if (peer.status != WifiP2pDevice.CONNECTED) {
                    connector!!.toBeConnectPeer(this,peer,null)
                }
            }
            communicator.toBeRun()
            DataConverter().toBeVisitedUnitToJson(this,unitInfo) {
                communicator.toBeWriteMessage(it)
            }
        }
    }
    private fun beStopDiscovering() {
        connector?.toBeStopDiscovering(this,null)
    }

    private fun beSaveUnitInfo(context: Context) {
        val json = convertAnyToJson(unitInfo)
        context.getSharedPreferences(
            Singleton.sharePrefsKey,
            Context.MODE_PRIVATE).applyEdit {
            putString("visited_unit_info", json)
        }
        convertSource(context)
    }

    private fun beRoleChanged(
        context: Context, subscriber: (String) -> Unit) {
        roleSubscriber = subscriber
        val sharePrefs = context.getSharedPreferences(
            Singleton.sharePrefsKey, Context.MODE_PRIVATE
        )
        val role: String =
            sharePrefs.getString("role_of_user", "") ?: ""
        if (role.isEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                roleSubscriber!!(
                    localized(context, R.string.role_changed)
                )
            }
        }
    }

    private fun beSwitchRole(context: Context) {
        val sharePrefs = context.getSharedPreferences(
            Singleton.sharePrefsKey, Context.MODE_PRIVATE)
        sharePrefs?.applyEdit {
            remove("role_of_user")
        }
        if (roleSubscriber != null) {
            CoroutineScope(Dispatchers.Main).launch {
                roleSubscriber!!(
                    localized(context, R.string.role_changed)
                )
            }
        }
    }
    private fun beDestroyService() {
        connector?.toBeRemoveGroup(this) { removed ->
            if (removed)
                connector!!.toBeDisconnect(this, null)
            connector?.toBeDestroyService()
        }
    }

    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeSubscribeSource(
        context: Context, subscriber: (List<ListEditItem>) -> Unit) {
        send {
            beSubscribeSource(context, subscriber)
        }
    }

    fun toBeCheckPermission(
        context: Context, complete: (Boolean) -> Unit) {
        send {
            beCheckPermission(context, complete)
        }
    }

    fun toBeRequestPermission(activity: Activity) {
        send {
            beRequestPermission(activity)
        }
    }

    fun toBeEnableWifiDirect(
        context: Context, complete: (Boolean) -> Unit) {
        send {
            beEnableWifiDirect(context, complete)
        }
    }
    fun toBeDiscoverPeers() {
        send {
            beDiscoverPeers()
        }
    }
    fun toBeStopDiscovering() {
        send {
            beStopDiscovering()
        }
    }
    fun toBeSaveUnitInfo(context: Context) {
        send {
            beSaveUnitInfo(context)
        }
    }

    fun toBeRoleChanged(
        context: Context, subscriber: (String) -> Unit) {
        send {
            beRoleChanged(context, subscriber)
        }
    }

    fun toBeSwitchRole(context: Context) {
        send {
            beSwitchRole(context)
        }
    }
    fun toBeDestroyService() {
        send {
            beDestroyService()
        }
    }

    /* --------------------------------------------------------------------- */
    // MARK: - Private
    fun toBeSubscribeRedux(
        subscriber: (Action) -> Unit) {
        send {
            beSubscribeRedux(subscriber)
        }
    }

    fun toBeUnSubscribeRedux() {
        send {
            beUnSubscribeRedux()
        }
    }

    private fun convertSource(context: Context) {
        val source = listOf(
            ListEditItem(
                title = "${localized(context, R.string.code)}:",
                placeholder = localized(context, R.string.please_input_your_name),
                keyboardType = InputType.TYPE_NUMBER_FLAG_DECIMAL,
                content = unitInfo.code
            ),
            ListEditItem(
                title = "${localized(context, R.string.name)}:",
                placeholder = localized(context, R.string.please_enter_your_phone_number),
                keyboardType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME,
                content = unitInfo.name
            ),
            ListEditItem(
                title = "${localized(context, R.string.form_url)}:",
                placeholder = localized(
                    context, R.string.please_fill_in_what_you_want_to_prefill
                ),
                keyboardType = InputType.TYPE_TEXT_VARIATION_URI,
                content = unitInfo.cloudForm
            )
        )
        sourceSubscriber?.let { it(source) }
    }

    /* --------------------------------------------------------------------- */
    // MARK: - Interface
    private val stateSubscriber = object : SceneSubscriber {
        override fun onNewState(state: SceneState) {
            when (state.currentAction) {
                is InputValueChangedAction -> {
                    val action =
                        state.currentAction as InputValueChangedAction
                    when (action.tag) {
                        0 -> {
                            unitInfo.code = action.value
                        }
                        1 -> {
                            unitInfo.name = action.value
                        }
                        2 -> {
                            unitInfo.cloudForm = action.value
                        }
                    }
                }
                is GetQrCodeAction -> {
                    val action =
                        state.currentAction as GetQrCodeAction
                    unitInfo.qrB64Image = action.b64Image
                    beSaveUnitInfo(action.context)
                }
            }
        }
    }
}
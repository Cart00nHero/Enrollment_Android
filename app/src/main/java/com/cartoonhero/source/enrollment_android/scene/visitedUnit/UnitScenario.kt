package com.cartoonhero.source.enrollment_android.scene.visitedUnit

import android.Manifest
import android.app.Activity
import android.content.Context
import android.net.wifi.p2p.WifiP2pDevice
import android.text.InputType
import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.actors.Conservator
import com.cartoonhero.source.actors.p2p.PeerCommunicator
import com.cartoonhero.source.actors.p2p.PeerConnector
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.props.Singleton
import com.cartoonhero.source.props.entities.ListEditItem
import com.cartoonhero.source.props.entities.VisitedUnit
import com.cartoonhero.source.props.inlineMethods.applyEdit
import com.cartoonhero.source.props.inlineMethods.convertAnyToJson
import com.cartoonhero.source.props.inlineMethods.toAny
import com.cartoonhero.source.props.localized
import com.cartoonhero.source.redux.ReduxFactory
import com.cartoonhero.source.redux.SceneState
import com.cartoonhero.source.redux.SceneSubscriber
import com.cartoonhero.source.redux.actions.InputValueChangedAction
import kotlinx.coroutines.*
import org.rekotlin.Action

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class UnitScenario: Actor() {

    private var connector:PeerConnector? = null
    private val communicator = PeerCommunicator()
    private val peerList: HashSet<WifiP2pDevice> = hashSetOf()
    private var unitInfo = VisitedUnit()
    private var sourceSubscriber: ((List<ListEditItem>) -> Unit)? = null
    private val redux = ReduxFactory()
    private var reduxStateEvent: ((Action) -> Unit)? = null

    private fun beSubscribeRedux(
        subscriber: (Action) -> Unit) {
        reduxStateEvent = subscriber
        redux.subscribeRedux(stateSubscriber)
    }

    private fun beUnSubscribeRedux() {
        redux.unSubscribe()
    }

    private fun beSubscribeSource(
        context: Context,subscriber: (List<ListEditItem>) -> Unit) {
        sourceSubscriber = subscriber
        val sharePrefs =
            context.getSharedPreferences(Singleton.sharePrefsKey, Context.MODE_PRIVATE)
        val json: String = sharePrefs.getString("visited_unit_info", "") ?: ""
        if (json.isNotEmpty()) {
            unitInfo = json.toAny<VisitedUnit>() ?: VisitedUnit()
        }
        convertSource(context)
    }
    private fun beCheckPermission(
        context: Context,complete: (Boolean) -> Unit) {
        Conservator().toBeCheckPermission(this,
            context, Manifest.permission.ACCESS_FINE_LOCATION) {
            CoroutineScope(Dispatchers.Main).launch {
                complete(it)
            }
        }
    }
    private fun beRequestPermission(activity: Activity) {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        Conservator().toBeRequestPermission(activity,permission)
    }

    private fun beSetUpConnection(
        context: Context, complete:(Boolean) -> Unit) {
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
    private fun beDisconnect() {
        connector?.toBeRemoveGroup(this) { removed ->
            if (removed)
                connector!!.toBeDisconnect(this,null)
        }
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
    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeSubscribeSource(
        context: Context,subscriber: (List<ListEditItem>) -> Unit) {
        send {
            beSubscribeSource(context, subscriber)
        }
    }
    fun toBeCheckPermission(
        context: Context,complete: (Boolean) -> Unit) {
        send {
            beCheckPermission(context, complete)
        }
    }
    fun toBeRequestPermission(activity: Activity) {
        send {
            beRequestPermission(activity)
        }
    }
    fun toBeSetUpConnection(
        context: Context, complete:(Boolean) -> Unit) {
        send {
            beSetUpConnection(context, complete)
        }
    }

    /* --------------------------------------------------------------------- */
    // MARK: - Private
    fun toBeSubscribeRedux(
        subscriber: (Action) -> Unit
    ) {
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
    fun toBeSaveUnitInfo(context: Context) {
        send {
            beSaveUnitInfo(context)
        }
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
            }
        }
    }
}
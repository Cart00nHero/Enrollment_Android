package com.cartoonhero.source.enrollment_android.scene.visitor

import android.Manifest
import android.app.Activity
import android.content.Context
import android.text.InputType
import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.actors.Conservator
import com.cartoonhero.source.actors.express.Courier
import com.cartoonhero.source.actors.wifiDirect.PeerCommunicator
import com.cartoonhero.source.actors.wifiDirect.PeerConnector
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.props.Singleton
import com.cartoonhero.source.props.entities.ListEditItem
import com.cartoonhero.source.props.entities.VisitedUnit
import com.cartoonhero.source.props.entities.VisitorInfo
import com.cartoonhero.source.props.inlineMethods.applyEdit
import com.cartoonhero.source.props.inlineMethods.convertAnyToJson
import com.cartoonhero.source.props.inlineMethods.toEntity
import com.cartoonhero.source.props.localized
import com.cartoonhero.source.redux.ReduxFactory
import com.cartoonhero.source.redux.SceneState
import com.cartoonhero.source.redux.SceneSubscriber
import com.cartoonhero.source.redux.actions.InputValueChangedAction
import kotlinx.coroutines.*
import org.rekotlin.Action

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi

class VisitorScenario : Actor() {
    private lateinit var connector: PeerConnector
    private val communicator = PeerCommunicator()
    private var visitor: VisitorInfo = VisitorInfo()
    private val redux = ReduxFactory()
    private var reduxStateEvent: ((Action) -> Unit)? = null
    private var sourceSubscriber: ((List<ListEditItem>) -> Unit)? = null
    private var roleSubscriber:((String) -> Unit)? = null

    private fun beSubscribeRedux(
        subscriber: (Action) -> Unit) {
        reduxStateEvent = subscriber
        redux.subscribeRedux(stateSubscriber)
    }

    private fun beUnSubscribeRedux() {
        redux.unSubscribe()
    }

    private fun beGetDataSource(
        context: Context, complete: (List<ListEditItem>) -> Unit) {
        sourceSubscriber = complete
        val sharePrefs =
            context.getSharedPreferences(
                Singleton.sharePrefsKey, Context.MODE_PRIVATE)
        val json: String =
            sharePrefs.getString("visitor_info", "") ?: ""
        if (json.isNotEmpty()) {
            visitor = json.toEntity<VisitorInfo>() ?: VisitorInfo()
        }
        convertVisitorSource(context)
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

    private fun beEnableP2PService(
        activity: Activity, complete: (Boolean) -> Unit) {
        connector = PeerConnector(activity)
        connector.toBeSetup(this) {
            if (it)
                communicator.toBeRun()
            complete(it)
        }
    }
    private fun beReadMessage() {
        communicator.toBeReadMessage(this) { message ->
            if (message.isNotEmpty()) {
                val unitInfo:VisitedUnit =
                    message.toEntity<VisitedUnit>() ?: VisitedUnit()
//                Courier().toBeApplyExpress(this,)
            }
        }
    }

    private fun beLeaveGroup() {
        connector.toBeRemoveGroup(this,null)
    }

    private fun beDestroyP2P() {
        connector.toBeDestroyService()
    }

    private fun beSaveVisitor(context: Context) {
        val json = convertAnyToJson(visitor)
        context.getSharedPreferences(
            Singleton.sharePrefsKey,
            Context.MODE_PRIVATE).applyEdit {
            putString("visitor_info", json)
        }
        convertVisitorSource(context)
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
                    localized(R.string.role_changed)
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
                    localized(R.string.role_changed)
                )
            }
        }
    }

    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
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

    fun toBeGetDataSource(
        context: Context, complete: (List<ListEditItem>) -> Unit) {
        send {
            beGetDataSource(context, complete)
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

    fun toBeEnableP2PService(
        activity: Activity, complete: (Boolean) -> Unit) {
        send {
            beEnableP2PService(activity, complete)
        }
    }
    fun toBeLeaveGroup() {
        send {
            beLeaveGroup()
        }
    }

    fun toBeDestroyP2P() {
        send {
            beDestroyP2P()
        }
    }

    fun toBeSaveVisitor(context: Context) {
        send {
            beSaveVisitor(context)
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

    /* --------------------------------------------------------------------- */
    // MARK: - Private
    private fun convertVisitorSource(context: Context) {
        val source = listOf(
            ListEditItem(
                title = "${localized(R.string.name)}:",
                placeholder = localized(R.string.please_input_your_name),
                keyboardType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME,
                content = visitor.name
            ),
            ListEditItem(
                title = "${localized(R.string.tel)}:",
                placeholder = localized(R.string.please_enter_your_phone_number),
                keyboardType = InputType.TYPE_CLASS_PHONE,
                content = visitor.tel
            ),
            ListEditItem(
                title = "${localized(R.string.info)}:",
                placeholder = localized(R.string.please_fill_in_what_you_want_to_prefill),
                keyboardType = InputType.TYPE_CLASS_TEXT,
                content = visitor.others
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
                            visitor.name = action.value
                        }
                        1 -> {
                            visitor.tel = action.value
                        }
                        2 -> {
                            visitor.others = action.value
                        }
                    }
                }
            }
        }
    }
}
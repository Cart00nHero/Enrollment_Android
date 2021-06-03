package com.cartoonhero.source.enrollment_android.scene.roleSelection

import android.content.Context
import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.props.inlineMethods.applyEdit
import com.cartoonhero.source.props.sharedPreferencesName
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class RoleSelectionScenario: Actor() {
    private fun beVisitor(context:Context ,complete:() -> Unit) {
        val sharePrefs = context.getSharedPreferences(
            sharedPreferencesName,Context.MODE_PRIVATE)
        sharePrefs.applyEdit {
            putString("role_of_user","Visitor")
        }
        CoroutineScope(Dispatchers.Main).launch {
            complete()
        }
    }
    private fun beVisitedUnit(context: Context,complete: () -> Unit) {
        val sharePrefs = context.getSharedPreferences(
            sharedPreferencesName,Context.MODE_PRIVATE)
        sharePrefs.applyEdit {
            putString("role_of_user","Visited_Unit")
        }
        CoroutineScope(Dispatchers.Main).launch {
            complete()
        }
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeVisitor(context:Context ,complete:() -> Unit) {
        send {
            beVisitor(context, complete)
        }
    }
    fun toBeVisitedUnit(context: Context,complete: () -> Unit) {
        send {
            beVisitedUnit(context, complete)
        }
    }
}
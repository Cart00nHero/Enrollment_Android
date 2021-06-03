package com.cartoonhero.source.redux

import org.rekotlin.Action
import org.rekotlin.StoreSubscriber

class ReduxFactory: StoreSubscriber<SceneState?> {
    var newStateAction: ((Action) -> Unit)? = null
    fun subscribeRedux(subscriber:(Action) -> Unit) {
        newStateAction = subscriber
        appStore.subscribe(this) { it ->
            it.select {
                it.sceneState
            }
        }
    }
    fun unSubscribe() {
        appStore.unsubscribe(this)
    }
    override fun newState(state: SceneState?) {
        if (newStateAction != null) {
            state?.currentAction?.let { newStateAction!!(it) }
        }
    }
}
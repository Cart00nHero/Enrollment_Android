package com.cartoonhero.source.redux

import org.rekotlin.StoreSubscriber

class ReduxFactory: StoreSubscriber<SceneState?> {
    private var stateSubscriber: SceneSubscriber? = null
    fun unSubscribe() {
        appStore.unsubscribe(this)
    }
    fun subscribeRedux(subscriber: SceneSubscriber) {
        stateSubscriber = subscriber
        appStore.subscribe(this) {
            it.select { state ->
                state.sceneState
            }
        }
    }
    override fun newState(state: SceneState?) {
        state?.let { stateSubscriber?.onNewState(it) }
    }
}
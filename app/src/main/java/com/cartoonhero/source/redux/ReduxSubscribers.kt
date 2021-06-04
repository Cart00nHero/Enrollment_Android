package com.cartoonhero.source.redux

interface SceneSubscriber {
    fun onNewState(state: SceneState)
}
package com.cartoonhero.source.redux

import org.rekotlin.Action

fun appReducer(action: Action, state: AppState?): AppState {
    return AppState(
        sceneState = sceneReducer(action, state?.sceneState)
    )
}

fun sceneReducer(action: Action, state: SceneState?): SceneState {
    val actState = state?: SceneState()
    return actState.copy(currentAction = action)
}
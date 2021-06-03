package com.cartoonhero.source.redux

import org.rekotlin.Action
import org.rekotlin.StateType
import org.rekotlin.Store

data class AppState(
    val sceneState: SceneState?
) : StateType

data class SceneState(
    var currentAction: Action? = null
) : StateType

val appStore = Store (
    reducer = ::appReducer,
    state = null
)
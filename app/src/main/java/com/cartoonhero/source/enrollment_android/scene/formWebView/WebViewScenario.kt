package com.cartoonhero.source.enrollment_android.scene.formWebView

import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.actors.express.Courier
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class WebViewScenario: Actor() {
    private fun beCollectUrl(complete:(String) -> Unit) {
        Courier().toBeClaim(this) { pSet ->
            for (parcel in pSet) {
                if (parcel.content is String) {
                    CoroutineScope(Dispatchers.Main).launch {
                        complete(parcel.content)
                    }
                }
            }
        }
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBeCollectUrl(complete:(String) -> Unit) {
        send {
            beCollectUrl(complete)
        }
    }
}
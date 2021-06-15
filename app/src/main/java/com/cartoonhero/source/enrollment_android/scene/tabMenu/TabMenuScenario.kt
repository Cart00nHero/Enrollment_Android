package com.cartoonhero.source.enrollment_android.scene.tabMenu

import android.content.Context
import com.cartoonhero.source.actormodel.Actor
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.enrollment_android.scene.formWebView.WebViewFragment
import com.cartoonhero.source.enrollment_android.scene.qrCode.QRCodeFragment
import com.cartoonhero.source.enrollment_android.scene.visitedUnit.UnitFragment
import com.cartoonhero.source.enrollment_android.scene.visitor.VisitorFragment
import com.cartoonhero.source.props.*
import com.cartoonhero.source.props.entities.TabMenuSource
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
@ObsoleteCoroutinesApi
class TabMenuScenario : Actor() {

    private fun bePrepareTabSources(complete: (TabMenuSource) -> Unit) {
        val source = TabMenuSource()
        val tabTitles = mutableListOf(
            localized( R.string.information),
            localized(R.string.web),
            "QRCode"
        )
        when (Singleton.instance.currentRole) {
            "Visitor" -> {
                source.menuResId = R.menu.menu_visitor_tab
                source.pages.addAll(
                    listOf(
                        VisitorFragment(), WebViewFragment(),QRCodeFragment())
                )
            }
            "Visited_Unit" -> {
                source.menuResId = R.menu.menu_unit_tab
                source.pages.addAll(
                    listOf(UnitFragment(), QRCodeFragment())
                )
                tabTitles.removeAt(1)
            }
        }
        for (i in 0 until source.pages.size) {
            val tabItem = TabLayoutItem()
            tabItem.title = tabTitles[i]
            source.tabItems.add(tabItem)
        }
        CoroutineScope(Dispatchers.Main).launch {
            complete(source)
        }
    }
    /* --------------------------------------------------------------------- */
    // MARK: - Portal Gate
    fun toBePrepareTabSources(
        complete: (TabMenuSource) -> Unit) {
        send {
            bePrepareTabSources(complete)
        }
    }
}
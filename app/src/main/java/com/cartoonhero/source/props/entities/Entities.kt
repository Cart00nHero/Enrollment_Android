package com.cartoonhero.source.props.entities

import androidx.fragment.app.Fragment
import com.cartoonhero.source.props.TabLayoutItem

data class ListEditItem(
    var title: String = "",
    var placeholder: String = "",
    var keyboardType: Int = 0,
    var content: String = ""
)

data class VisitorInfo(
    var name: String = "",
    var tel: String = "",
    var others: String = ""
)

data class VisitedUnit(
    var code: String = "",
    var name: String = "",
    var cloudForm: String = "",
    var qrB64Image: String = ""
)

data class TabMenuSource(
    var menuResId: Int = 0,
    val pages: MutableList<Fragment> = mutableListOf(),
    val tabItems: MutableList<TabLayoutItem> = mutableListOf()
)
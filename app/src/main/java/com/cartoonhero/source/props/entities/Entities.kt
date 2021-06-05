package com.cartoonhero.source.props.entities

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
    var code:String = "",
    var name: String = "",
    var cloudForm: String = "",
    var qrB64Image: String = ""
)
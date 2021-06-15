package com.cartoonhero.source.enrollment_android.scenery

import android.content.Context
import android.net.wifi.ScanResult
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.props.localized
import com.cartoonhero.source.redux.actions.WifiConnectBtnClickAction
import com.cartoonhero.source.redux.appStore
import kotlinx.android.synthetic.main.layout_edit_listitem.view.*
import kotlinx.android.synthetic.main.layout_text_field.view.*

class WifiScanItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        inflate(context, R.layout.layout_edit_listitem, this)
    }

    private var wifiScanResult: ScanResult? = null
    var wifiConnected = false
    fun initialized() {
        if (wifiConnected) {
            this.item_button.visibility = View.GONE
            return
        }
        this.item_button.visibility = View.VISIBLE
        this.item_button.text = localized(R.string.connect)
        this.item_textField.newTextSubscriber = { text ->
            if (text.isEmpty()) {
                this.item_button.text = localized(R.string.cancel)
            } else {
                this.item_button.text = "Go"
            }
        }
        this.item_button.setOnClickListener {
            val itemBtn = it as Button
            when(itemBtn.text) {
                localized(R.string.connect) -> {
                    this.item_button.text = localized(R.string.cancel)
                    this.item_contentText.visibility = View.GONE
                    this.item_textField.visibility = View.VISIBLE
                    this.text_field.hint =
                        localized(R.string.please_input_passphrase)
                }
                localized(R.string.cancel) -> {
                    this.item_button.text = localized(R.string.connect)
                    this.item_contentText.visibility = View.VISIBLE
                    this.item_textField.visibility = View.GONE
                }
                "Go" -> {
                    val name = wifiScanResult?.SSID ?: ""
                    val pass: String = this.text_field.text.toString()
                    appStore.dispatch(
                        WifiConnectBtnClickAction(this,name,pass))
                    this.item_button.text = localized(R.string.connect)
                    this.item_contentText.visibility = View.VISIBLE
                    this.item_textField.visibility = View.GONE
                }
            }
        }
    }

    fun bindScanResult(result: ScanResult) {
        wifiScanResult = result
        this.item_contentText.text = result.SSID
        this.item_button.text = localized(R.string.connect)
    }
}
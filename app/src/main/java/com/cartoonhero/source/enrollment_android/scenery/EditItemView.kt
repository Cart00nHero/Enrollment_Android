package com.cartoonhero.source.enrollment_android.scenery

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.props.Singleton
import com.cartoonhero.source.props.entities.ListEditItem
import com.cartoonhero.source.redux.actions.InputValueChangedAction
import com.cartoonhero.source.redux.appStore
import kotlinx.android.synthetic.main.layout_edit_listitem.view.*
import kotlinx.android.synthetic.main.layout_text_field.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

class EditItemView<T> @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var currentContent = ""
    init {
        inflate(context,R.layout.layout_edit_listitem,this)
    }
    @SuppressLint("SetTextI18n")
    inline fun <reified T> bindItemData(data: T, isEditor: Boolean) {
        when(data) {
            is ListEditItem -> {
                this.item_titleText.text = data.title
                if (isEditor) {
                    item_contentText.visibility = View.GONE
                    item_textField.visibility = View.VISIBLE
                    item_textField.text_field.hint = data.placeholder
                    item_textField.text_field.setText(data.content)
                    item_textField.text_field.imeOptions = EditorInfo.IME_ACTION_DONE
                    item_textField.text_field.setTextSize(
                        TypedValue.COMPLEX_UNIT_SP,14.0F)
                    item_textField.newTextSubscriber = {
                        appStore.dispatch(InputValueChangedAction(this.tag as Int,it))
                    }
                } else {
                    item_textField.visibility = View.GONE
                    item_contentText.visibility = View.VISIBLE
                    item_contentText.text = data.content
                    currentContent = data.content
                    item_contentText.setTextSize(TypedValue.COMPLEX_UNIT_SP,14.0F)
                    item_contentText.gravity = Gravity.CENTER_VERTICAL
                }
            }
        }
        this.item_copyButton.setOnClickListener {
            val clipboard =
                context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // Creates a new text clip to put on the clipboard
            val clip: ClipData =
                ClipData.newPlainText(Singleton.clipLabel, currentContent)
            // Set the clipboard's primary clip.
            clipboard.setPrimaryClip(clip)
            (it as Button).text = "Copied"
            Timer().schedule(500) {
                CoroutineScope(Dispatchers.Main).launch {
                    it.text = "Copy"
                }
            }
        }
    }
}
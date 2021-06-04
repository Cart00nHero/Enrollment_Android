package com.cartoonhero.source.enrollment_android.scenery

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.props.Singleton
import com.cartoonhero.source.props.entities.ListEditItem
import com.cartoonhero.source.props.match
import com.cartoonhero.source.redux.actions.InputValueChangedAction
import com.cartoonhero.source.redux.appStore
import kotlinx.android.synthetic.main.listitem_edit.view.*
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
        inflate(context,R.layout.listitem_edit,this)
    }
    @SuppressLint("SetTextI18n")
    inline fun <reified T> bindItemData(data: T, isEditor: Boolean) {
        when(data) {
            is ListEditItem -> {
                this.item_titleText.text = data.title
                if (isEditor) {
                    item_copyButton.visibility = View.GONE
                    val editText = EditText(context)
                    setMatchConstraints(editText)
                    editText.layoutParams.width = 0
                    editText.layoutParams.height = 0
                    editText.hint = data.placeholder
                    editText.setText(data.content)
                    editText.inputType = data.keyboardType
                    editText.imeOptions = EditorInfo.IME_ACTION_DONE
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14.0F)
                    editText.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?, start: Int,
                            count: Int, after: Int) {
                        }

                        override fun onTextChanged(
                            s: CharSequence?, start: Int,
                            before: Int, count: Int) {
                            appStore.dispatch(
                                InputValueChangedAction(
                                    this@EditItemView.tag as Int,s.toString()))
                        }

                        override fun afterTextChanged(s: Editable?) {
                        }

                    })
                } else {
                    item_copyButton.visibility = View.VISIBLE
                    val textView = TextView(context)
                    setMatchConstraints(textView)
                    textView.layoutParams.width = 0
                    textView.layoutParams.height = 0
                    textView.text = data.content
                    currentContent = data.content
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14.0F)
                    textView.gravity = Gravity.CENTER_VERTICAL
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
     fun setMatchConstraints(content: View) {
        this.content_layout.removeAllViews()
        content.id = View.generateViewId()
        this.content_layout.addView(content)
        val set = ConstraintSet()
        set.clone(this.content_layout)
        set.match(content,this.content_layout)
        set.match(content, this.content_layout)
        // optionally, apply the constraints smoothly
        TransitionManager.beginDelayedTransition(this)
        set.applyTo(this.content_layout)
    }
}
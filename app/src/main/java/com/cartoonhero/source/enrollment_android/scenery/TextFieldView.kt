package com.cartoonhero.source.enrollment_android.scenery

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.cartoonhero.source.enrollment_android.R
import com.cartoonhero.source.props.interfaces.FieldTextWatcher
import kotlinx.android.synthetic.main.layout_text_field.view.*

class TextFieldView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    var newTextSubscriber: ((String) -> Unit)? = null
    var fieldTextWatcher: FieldTextWatcher? = null

    init {
        inflate(context, R.layout.layout_text_field, this)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initializeLayout()
    }

    private fun initializeLayout() {
        this.field_clear_button.setOnClickListener {
            this.text_field.setText("")
        }
        this.text_field.addTextChangedListener(textWatcher)
    }


    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            fieldTextWatcher?.beforeTextChanged(s, start, count, after)
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            newTextSubscriber?.let { it(s.toString()) }
            fieldTextWatcher?.onTextChanged(s, start, before, count)
        }

        override fun afterTextChanged(s: Editable?) {
            if (s == null || s.isEmpty()) {
                this@TextFieldView.field_clear_button.visibility = View.GONE
            } else {
                this@TextFieldView.field_clear_button.visibility = View.VISIBLE
            }
            fieldTextWatcher?.afterTextChanged(s)
        }

    }
}
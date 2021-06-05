package com.cartoonhero.source.props.interfaces

import android.text.Editable

interface FieldTextWatcher {
    fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int)

    fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int)

    fun afterTextChanged(s: Editable?)
}
package com.capstone.cleanup.ui.custom

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.capstone.cleanup.R

class CustomEditTextPass : AppCompatEditText {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init(){
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isValidPassword(s.toString())) {
                    setError(
                        context.getString(R.string.password_error),
                        null
                    )
                } else {
                    error = null
                }
            }
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    fun isValidPassword(pass: String): Boolean {
        return  pass.isNotEmpty() && pass.length >= 8
    }
}
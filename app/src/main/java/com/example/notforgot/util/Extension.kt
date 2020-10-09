package com.example.notforgot.util

import android.content.Context
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

fun Context.showShortText(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.writeToken(token: String) {
    SharedPref.writeApiToken(this, token)
}

fun Context.getToken(): String {
    return SharedPref.getApiToken(this)
}

fun Long.toDateString(): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    val netDate = Date(this)
    return sdf.format(netDate)
}

fun Long.fromMsToEpoch(): Long {
    return this.div(1000)
}

fun Long.fromEpochToMs(): Long {
    return this.times(1000)
}

fun String.isMail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(
        this
    ).matches()
}

fun TextInputEditText.invalidateError(layout: TextInputLayout) {
    this.doAfterTextChanged {
        if (layout.error != null)
            layout.error = null
    }
}
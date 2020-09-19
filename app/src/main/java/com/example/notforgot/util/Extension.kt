package com.example.notforgot.util

import android.content.Context
import android.widget.Toast

fun Context.showShortText(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Context.writeToken(token: String) {
    SharedPref.writeApiToken(this, token)
}

fun Context.getToken(): String {
    return SharedPref.getApiToken(this)
}
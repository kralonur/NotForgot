package com.example.notforgot.util

import android.content.Context
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LiveData
import androidx.navigation.NavBackStackEntry
import androidx.navigation.fragment.findNavController
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
        layout.invalidateError()
    }
}

fun TextInputLayout.invalidateError() {
    if (this.error != null)
        this.error = null
}

fun <T> Fragment.setNavigationResult(key: String, value: T) {
    findNavController().previousBackStackEntry?.savedStateHandle?.set(
        key,
        value
    )
}

fun <T> Fragment.getNavigationResult(
    navBackStackEntry: NavBackStackEntry,
    key: String,
    onResult: (result: T) -> Unit
) {

    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_RESUME
            && navBackStackEntry.savedStateHandle.contains(key)
        ) {
            val result = navBackStackEntry.savedStateHandle.get<T>(key)
            result?.let(onResult)
            navBackStackEntry.savedStateHandle.remove<T>(key)
        }
    }
    navBackStackEntry.lifecycle.addObserver(observer)

    viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            navBackStackEntry.lifecycle.removeObserver(observer)
        }
    })
}

fun <T> LiveData<T>.getNonNullValue() =
    requireNotNull(this.value)

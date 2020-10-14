package com.example.notforgot.ui.login

interface LoginValidation {
    fun validateEmail(validationMessage: String)
    fun validatePassword(validationMessage: String)
}
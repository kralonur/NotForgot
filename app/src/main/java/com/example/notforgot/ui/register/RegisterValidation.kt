package com.example.notforgot.ui.register

interface RegisterValidation {
    fun validateEmail(validationMessage: String)
    fun validatePassword(validationMessage: String)
    fun validateName(validationMessage: String)
}
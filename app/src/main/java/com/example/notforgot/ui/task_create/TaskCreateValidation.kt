package com.example.notforgot.ui.task_create

interface TaskCreateValidation {
    fun validateTitle(validationMessage: String)
    fun validateDescription(validationMessage: String)
    fun validateCategory(validationMessage: String)
    fun validatePriority(validationMessage: String)
    fun validateEndDate(validationMessage: String)
}
package com.example.notforgot.model

sealed class ResultWrapper<out T> {
    object Loading : ResultWrapper<Nothing>()
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    object Error : ResultWrapper<Nothing>()
}
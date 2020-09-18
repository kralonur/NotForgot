package com.example.notforgot.api

import com.example.notforgot.api.service.AuthService
import com.example.notforgot.util.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

private fun getHTTPClient(): OkHttpClient {
    Timber.i("getHTTPClient called")
    return OkHttpClient.Builder()
        .build()
}

private fun getMoshi(): Moshi {
    Timber.i("getMoshi called")
    return Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}

private fun getRetrofit(): Retrofit {
    Timber.i("getRetrofit called")
    return Retrofit.Builder()
        .client(getHTTPClient())
        .baseUrl(Constants.Api.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(getMoshi()))
        .build()
}

private inline fun <reified T> createService(): T =
    getRetrofit().create(T::class.java)

object NetworkService {
    val authService by lazy { createService<AuthService>() }
}
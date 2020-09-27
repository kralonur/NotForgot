package com.example.notforgot.api

import android.content.Context
import com.example.notforgot.api.service.AuthService
import com.example.notforgot.api.service.ItemsService
import com.example.notforgot.util.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

private fun getLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BASIC)
}

private fun getHTTPClient(context: Context): OkHttpClient {
    Timber.i("getHTTPClient called")
    return OkHttpClient.Builder()
        .addInterceptor(RequestInterceptor(context))
        .addInterceptor(getLoggingInterceptor())
        .build()
}

private fun getMoshi(): Moshi {
    Timber.i("getMoshi called")
    return Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
}

private fun getRetrofit(context: Context): Retrofit {
    Timber.i("getRetrofit called")
    return Retrofit.Builder()
        .client(getHTTPClient(context))
        .baseUrl(Constants.Api.BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(getMoshi()))
        .build()
}

private inline fun <reified T> createService(context: Context): T =
    getRetrofit(context).create(T::class.java)

object NetworkService {
    fun getAuthService(context: Context) = createService<AuthService>(context)

    fun getItemsService(context: Context) = createService<ItemsService>(context)
}
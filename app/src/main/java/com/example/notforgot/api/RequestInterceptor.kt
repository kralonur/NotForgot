package com.example.notforgot.api

import android.content.Context
import com.example.notforgot.util.getToken
import okhttp3.Interceptor
import okhttp3.Response

class RequestInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        requestBuilder.addHeader(
            "Authorization",
            "Bearer ${context.getToken()}"
        )

        return chain.proceed(requestBuilder.build())
    }
}
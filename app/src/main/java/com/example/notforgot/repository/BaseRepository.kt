package com.example.notforgot.repository

import com.example.notforgot.model.ResultWrapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

abstract class BaseRepository {
    fun <T> flowCall(apiCall: suspend () -> T): Flow<ResultWrapper<T>> = flow {
        emit(ResultWrapper.Loading)
        try {
            emit(ResultWrapper.Success(apiCall.invoke()))
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            when (throwable) {
                is IOException -> emit(ResultWrapper.NetworkError)
                is HttpException -> {
                    emit(ResultWrapper.ServerError(throwable.code()))
                }
                else -> {
                    emit(ResultWrapper.Error)
                }
            }
        }
    }

//    private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
//        return try {
//            throwable.response()?.errorBody()?.source()?.let {
//                val moshiAdapter = Moshi.Builder().build().adapter(ErrorResponse::class.java)
//                moshiAdapter.fromJson(it)
//            }
//        } catch (exception: Exception) {
//            null
//        }
//    }
}
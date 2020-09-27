package com.example.notforgot.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import timber.log.Timber

object SharedPref {

    private const val API_PREF_NAME = "ApiPreferences"
    private const val CONST_API_TOKEN = "API_TOKEN"

    private const val DB_PREF_NAME = "DbPreferences"
    private const val CONST_TASK_ID = "TASK_ID"
    private const val CONST_CATEGORY_ID = "CATEGORY_ID"

    //EncryptedSharedPreferences

    private fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            API_PREF_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun writeApiToken(context: Context, token: String) {
        getEncryptedSharedPreferences(context).edit {
            putString(CONST_API_TOKEN, token)
        }
        Timber.i("Written token: $token")
    }

    fun getApiToken(context: Context): String {
        val token = getEncryptedSharedPreferences(context).getString(CONST_API_TOKEN, "") ?: ""
        Timber.i("Retrieved token: $token")
        return token
    }


    //SharedPreferences

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(DB_PREF_NAME, Context.MODE_PRIVATE)
    }

    fun getTaskId(context: Context): Int {
        val sp = getSharedPreferences(context)
        if (!sp.contains(CONST_TASK_ID))
            sp.edit { putInt(CONST_TASK_ID, -1) }

        val currentId = sp.getInt(CONST_TASK_ID, -1)
        sp.edit { putInt(CONST_TASK_ID, currentId - 1) }

        return currentId
    }

    fun getCategoryId(context: Context): Int {
        val sp = getSharedPreferences(context)
        if (!sp.contains(CONST_CATEGORY_ID))
            sp.edit { putInt(CONST_CATEGORY_ID, -1) }

        val currentId = sp.getInt(CONST_CATEGORY_ID, -1)
        sp.edit { putInt(CONST_CATEGORY_ID, currentId - 1) }

        return currentId
    }

}
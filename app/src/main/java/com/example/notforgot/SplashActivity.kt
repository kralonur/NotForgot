package com.example.notforgot

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notforgot.util.getToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        GlobalScope.launch {
            delay(2000)

            val token = applicationContext.getToken()
            if (token.isEmpty() || token.isBlank()) {
                navigateToLogin()
            } else {
                navigateToMain()
            }
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this@SplashActivity, AuthActivity::class.java))
        finish()
    }

    private fun navigateToMain() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
}
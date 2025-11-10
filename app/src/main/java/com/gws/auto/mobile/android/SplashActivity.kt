package com.gws.auto.mobile.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        // Always navigate to MainActivity, login is no longer mandatory at startup.
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

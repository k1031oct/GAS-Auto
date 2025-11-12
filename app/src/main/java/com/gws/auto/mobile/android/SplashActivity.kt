package com.gws.auto.mobile.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.preference.PreferenceManager
import com.gws.auto.mobile.android.ui.wizard.WizardActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isFirstRun = prefs.getBoolean("is_first_run", true)

        if (isFirstRun) {
            startActivity(Intent(this, WizardActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}

package com.gws.auto.mobile.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.gws.auto.mobile.android.data.repository.SettingsRepository
import com.gws.auto.mobile.android.ui.wizard.WizardActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { true }

        lifecycleScope.launch {
            val isWizardCompleted = settingsRepository.isWizardCompleted.first()
            val intent = if (isWizardCompleted) {
                Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashActivity, WizardActivity::class.java)
            }
            startActivity(intent)
            finish()
        }
    }
}

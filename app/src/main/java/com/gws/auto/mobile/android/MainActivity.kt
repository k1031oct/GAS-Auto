package com.gws.auto.mobile.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.analytics.FirebaseAnalytics
import com.gws.auto.mobile.android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var analytics: FirebaseAnalytics
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

//        auth = FirebaseAuth.getInstance()
//        if (auth.currentUser == null) {
//            startActivity(Intent(this, SignInActivity::class.java))
//            finish()
//            return
//        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        analytics = FirebaseAnalytics.getInstance(this)

        val navController = findNavController(R.id.nav_host_fragment)
        binding.bottomNavView.setupWithNavController(navController)
    }
}

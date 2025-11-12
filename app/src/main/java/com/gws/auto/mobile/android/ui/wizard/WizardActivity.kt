package com.gws.auto.mobile.android.ui.wizard

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.gws.auto.mobile.android.MainActivity
import com.gws.auto.mobile.android.R
import com.gws.auto.mobile.android.databinding.ActivityWizardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WizardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWizardBinding
    private val viewModel: WizardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWizardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pagerAdapter = WizardPagerAdapter(this)
        binding.wizardViewPager.adapter = pagerAdapter

        binding.wizardViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.backButton.isEnabled = position > 0
                if (position == pagerAdapter.itemCount - 1) {
                    binding.nextButton.setText(R.string.wizard_finish)
                } else {
                    binding.nextButton.setText(R.string.wizard_next)
                }
            }
        })

        binding.nextButton.setOnClickListener {
            if (binding.wizardViewPager.currentItem < pagerAdapter.itemCount - 1) {
                binding.wizardViewPager.currentItem += 1
            } else {
                viewModel.finishWizard()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        binding.backButton.setOnClickListener {
            binding.wizardViewPager.currentItem -= 1
        }
    }
}

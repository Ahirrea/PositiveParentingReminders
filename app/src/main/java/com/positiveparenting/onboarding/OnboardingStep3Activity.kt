package com.positiveparenting.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.positiveparenting.R

class OnboardingStep3Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_step3)

        val understoodButton: Button = findViewById(R.id.understood_button)
        understoodButton.setOnClickListener {
            val intent = Intent(this, AccountCreationActivity::class.java)
            startActivity(intent)
        }
    }
}

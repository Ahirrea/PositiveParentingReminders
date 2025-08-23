package com.positiveparenting.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.positiveparenting.R

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val letsGoButton: Button = findViewById(R.id.lets_go_button)
        letsGoButton.setOnClickListener {
            val intent = Intent(this, OnboardingStep2Activity::class.java)
            startActivity(intent)
        }
    }
}

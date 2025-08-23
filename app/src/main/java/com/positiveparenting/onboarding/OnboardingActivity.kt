package com.positiveparenting.onboarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.positiveparenting.R

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val letsGoButton: Button = findViewById(R.id.lets_go_button)
        letsGoButton.setOnClickListener {
            Toast.makeText(this, "Let's go!", Toast.LENGTH_SHORT).show()
            // Here you would navigate to the next activity
        }
    }
}

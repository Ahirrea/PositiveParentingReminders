package com.positiveparenting.onboarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.positiveparenting.R

class OnboardingStep2Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_step2)

        val nextButton: Button = findViewById(R.id.next_button)
        nextButton.setOnClickListener {
            Toast.makeText(this, "Next button clicked!", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to the next screen
        }
    }
}

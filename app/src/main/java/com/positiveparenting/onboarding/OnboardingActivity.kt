package com.positiveparenting.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.positiveparenting.R
import com.positiveparenting.journal.JournalEditorActivity
import com.positiveparenting.profile.LocalProfileStore

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Onboarding runs once: after it is complete (flag set by the profile
        // step, A-10) every launch goes straight into the editor (A-1).
        if (LocalProfileStore(this).isOnboardingComplete) {
            startActivity(Intent(this, JournalEditorActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_onboarding)

        val letsGoButton: Button = findViewById(R.id.lets_go_button)
        letsGoButton.setOnClickListener {
            val intent = Intent(this, OnboardingStep2Activity::class.java)
            startActivity(intent)
        }
    }
}

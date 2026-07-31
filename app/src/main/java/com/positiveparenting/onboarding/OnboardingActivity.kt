package com.positiveparenting.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.positiveparenting.R
import com.positiveparenting.journal.JournalEditorActivity

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Onboarding runs once; afterwards every app start goes straight to the editor.
        if (OnboardingPrefs.isOnboardingComplete(this)) {
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

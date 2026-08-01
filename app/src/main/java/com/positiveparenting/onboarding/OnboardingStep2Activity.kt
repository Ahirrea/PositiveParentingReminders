package com.positiveparenting.onboarding

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.content.Intent
import com.positiveparenting.R

class OnboardingStep2Activity : AppCompatActivity() {

    private var heartAnimator: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding_step2)

        // The heart is a static vector (unlike the Lottie files on steps 1
        // and 3), so it gets a gentle breathing pulse in code.
        val heart: ImageView = findViewById(R.id.icon_imageview)
        heartAnimator = ObjectAnimator.ofPropertyValuesHolder(
            heart,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.08f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.08f),
        ).apply {
            duration = 1200
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        val nextButton: Button = findViewById(R.id.next_button)
        nextButton.setOnClickListener {
            val intent = Intent(this, OnboardingStep3Activity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        heartAnimator?.cancel()
        heartAnimator = null
        super.onDestroy()
    }
}

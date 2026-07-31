package com.positiveparenting.onboarding

import android.content.Context

/**
 * Local flag marking the onboarding flow as finished. Once set, the launcher
 * ([OnboardingActivity]) forwards straight to the journal editor on every start.
 */
object OnboardingPrefs {

    private const val PREFS_NAME = "positive_parenting"
    private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"

    fun isOnboardingComplete(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ONBOARDING_COMPLETE, false)

    fun setOnboardingComplete(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ONBOARDING_COMPLETE, true)
            .apply()
    }
}

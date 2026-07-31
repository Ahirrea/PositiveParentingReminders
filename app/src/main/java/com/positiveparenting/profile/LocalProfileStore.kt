package com.positiveparenting.profile

import android.content.Context
import android.content.SharedPreferences

/**
 * SharedPreferences-backed storage for the local profile. Nothing here may
 * ever leave the device (ADR-002: no backend, no cloud, no account).
 */
class LocalProfileStore(context: Context) {

    private val prefs: SharedPreferences = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Persists the profile and marks onboarding as complete — saving the
     * profile is the final onboarding step. The launcher redirect that reads
     * [isOnboardingComplete] arrives with A-1.
     */
    fun save(profile: LocalProfile) {
        prefs.edit()
            .putString(KEY_PARENT_NAME, profile.parentName)
            .putString(KEY_CHILD_NAME, profile.childName)
            .putBoolean(KEY_ONBOARDING_COMPLETE, true)
            .apply()
    }

    fun load(): LocalProfile? {
        val parentName = prefs.getString(KEY_PARENT_NAME, null) ?: return null
        return LocalProfile(parentName, prefs.getString(KEY_CHILD_NAME, null))
    }

    val isOnboardingComplete: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)

    companion object {
        private const val PREFS_NAME = "local_profile"
        private const val KEY_PARENT_NAME = "parent_name"
        private const val KEY_CHILD_NAME = "child_name"
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    }
}

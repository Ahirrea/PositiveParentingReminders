package com.positiveparenting.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.positiveparenting.profile.LocalProfileStore

/**
 * Re-arms the daily reminder after events that clear or shift alarms: reboot
 * (alarms do not survive it) and clock or time-zone changes (the alarm is
 * stored as a UTC instant, so 20:00 local would drift otherwise). Does
 * nothing before onboarding is complete — arming happens on the first editor
 * launch.
 */
class ReminderRescheduleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!LocalProfileStore(context).isOnboardingComplete) return
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            -> ReminderScheduler.schedule(context)
        }
    }
}

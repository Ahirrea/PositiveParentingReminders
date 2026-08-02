package com.positiveparenting.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * Arms the daily reminder (A-3) as an inexact repeating alarm around
 * [REMINDER_TIME] local time. Inexact is deliberate: a gentle evening nudge
 * needs no exact-alarm permission and lets the system batch for battery.
 * Scheduling is idempotent — the PendingIntent identity replaces any
 * previously armed alarm. The time becomes configurable with A-6.
 */
object ReminderScheduler {

    /** Fixed default until A-6 makes it configurable: 20:00 local time. */
    val REMINDER_TIME: LocalTime = LocalTime.of(20, 0)

    fun schedule(context: Context) {
        val trigger = ReminderTimeCalculator.nextTrigger(LocalDateTime.now(), REMINDER_TIME)
        val triggerMillis = trigger.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        context.getSystemService(AlarmManager::class.java).setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerMillis,
            AlarmManager.INTERVAL_DAY,
            PendingIntent.getBroadcast(
                context,
                REQUEST_CODE,
                Intent(context, DailyReminderReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            ),
        )
    }

    private const val REQUEST_CODE = 0
}

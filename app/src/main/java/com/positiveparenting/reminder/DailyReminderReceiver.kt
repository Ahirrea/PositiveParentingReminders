package com.positiveparenting.reminder

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.positiveparenting.R
import com.positiveparenting.data.AppDatabase
import com.positiveparenting.journal.JournalEditorActivity
import com.positiveparenting.journal.PromptProvider
import com.positiveparenting.profile.LocalProfileStore
import java.time.LocalDate
import java.time.ZoneId
import kotlin.concurrent.thread

/**
 * Fires once a day (armed by [ReminderScheduler]): shows the day's prompt as
 * a purely local notification whose tap opens the editor. Skips silently when
 * an entry for today already exists — the reminder supports the habit, it
 * must not nag (PRD risk "journaling becomes a chore"). At most one
 * notification exists at a time (fixed id); an unread one is replaced, never
 * stacked.
 */
class DailyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Before onboarding is complete a notification would lead past the
        // profile step straight into the editor — never remind that early.
        if (!LocalProfileStore(context).isOnboardingComplete) return
        if (context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) return

        // Room must not be touched on the main thread; goAsync keeps the
        // receiver alive for the short count query.
        val pendingResult = goAsync()
        thread {
            try {
                val startOfToday = LocalDate.now().atStartOfDay(ZoneId.systemDefault())
                    .toInstant().toEpochMilli()
                val entriesToday = AppDatabase.get(context).journalEntryDao()
                    .countSinceBlocking(startOfToday)
                if (entriesToday == 0) {
                    showNotification(context)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(context: Context) {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.reminder_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = context.getString(R.string.reminder_channel_description)
            },
        )

        // Same source and formula as the editor, so notification and editor
        // inevitably show the same prompt. Generic content only — the
        // notification is visible on the lock screen, entry data never is.
        val prompt = PromptProvider.promptForDay(
            LocalDate.now().toEpochDay(),
            context.resources.getStringArray(R.array.daily_prompts).toList(),
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_heart)
            .setContentTitle(context.getString(R.string.reminder_notification_title))
            .setContentText(prompt)
            .setStyle(NotificationCompat.BigTextStyle().bigText(prompt))
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, JournalEditorActivity::class.java),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                ),
            )
            .setAutoCancel(true)
            .build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val CHANNEL_ID = "daily_reminder"
        private const val NOTIFICATION_ID = 1
    }
}

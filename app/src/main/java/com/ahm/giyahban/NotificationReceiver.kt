package com.ahm.giyahban

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: android.content.Intent) {
        val id = intent.getLongExtra("reminder_id", -1L)
        if (id < 0) return

        val pendingResult = goAsync()
        val executor = java.util.concurrent.Executors.newSingleThreadExecutor()

        executor.execute {
            try {
                val reminder = AppDatabase.get(context).reminderDao().getById(id) ?: return@execute
                createChannel(context)

                val canNotify = Build.VERSION.SDK_INT < 33 ||
                        androidx.core.content.ContextCompat.checkSelfPermission(
                            context, Manifest.permission.POST_NOTIFICATIONS
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                if (canNotify) {
                    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle(reminder.title)
                        .setContentText(reminder.message)
                        .setStyle(NotificationCompat.BigTextStyle().bigText(reminder.message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .build()

                    NotificationManagerCompat.from(context)
                        .notify(id.toInt(), notification)

                    // One-shot reminder: remove it after successful delivery attempt.
                    AppDatabase.get(context).reminderDao().delete(reminder)
                }
            } finally {
                executor.shutdown()
                pendingResult.finish()
            }
        }
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Scheduled reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for scheduled reminders"
            }
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "scheduled_reminders"
    }
}

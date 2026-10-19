package com.ahm.giyahban

import android.Manifest
import android.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getLongExtra("task_id", -1L)
        if (id < 0) return

        val pendingResult = goAsync()
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {
            try {
                val db = TaskDatabaseHelper(context)
                val task = db.getTaskById(id) ?: return@execute
                createChannel(context)

                val canNotify = Build.VERSION.SDK_INT < 33 ||
                        ContextCompat.checkSelfPermission(
                            context, Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED

                if (canNotify) {
                    val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_dialog_info)
                        .setContentTitle(task.title)
                        .setContentText(task.message)
                        .setStyle(NotificationCompat.BigTextStyle().bigText(task.message))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .build()

                    NotificationManagerCompat.from(context)
                        .notify(id.toInt(), notification)

                    // One-shot reminder: remove it after successful delivery attempt.
                    db.deleteTask(id)
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

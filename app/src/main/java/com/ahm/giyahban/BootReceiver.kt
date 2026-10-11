package com.ahm.giyahban

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED &&
            action != Intent.ACTION_LOCKED_BOOT_COMPLETED &&
            action != Intent.ACTION_MY_PACKAGE_REPLACED &&
            action != "android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED"
        ) return

        val pendingResult = goAsync()
        val executor = java.util.concurrent.Executors.newSingleThreadExecutor()

        executor.execute {
            try {
                val db = AppDatabase.get(context)
                val reminders = db.reminderDao().getFuture(System.currentTimeMillis())

                reminders.forEach { ReminderScheduler.schedule(context, it) }
            } finally {
                executor.shutdown()
                pendingResult.finish()
            }
        }
    }
}

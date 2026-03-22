package com.example.fintrack.ui.finance

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.fintrack.data.local.FinTrackDatabase
import java.util.Calendar
import java.util.concurrent.TimeUnit

class SmartReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val db = FinTrackDatabase.getInstance(applicationContext)
        val subscriptions = db.subscriptionDao().getAll().filter { it.isActive }
        if (subscriptions.isEmpty()) return Result.success()

        val dueSoon = subscriptions
            .map { sub -> sub to daysUntilDayOfMonth(sub.dayOfMonth) }
            .filter { (_, days) -> days in 0..3 }
            .sortedBy { (_, days) -> days }

        if (dueSoon.isEmpty()) return Result.success()

        val signature = dueSoon.joinToString("|") { (sub, days) -> "${sub.id}:$days" }
        val prefs = applicationContext.getSharedPreferences("smart_reminder_worker_prefs", Context.MODE_PRIVATE)
        val lastSignature = prefs.getString("last_signature", null)
        val lastTime = prefs.getLong("last_time", 0L)
        val now = System.currentTimeMillis()

        val withinCooldown = (now - lastTime) < 12L * 60L * 60L * 1000L
        if (signature == lastSignature && withinCooldown) return Result.success()

        val message = dueSoon.take(3).joinToString("\n") { (sub, days) ->
            val whenText = when (days) {
                0 -> "hoy"
                1 -> "mañana"
                else -> "en $days días"
            }
            "• ${sub.name} se cobra $whenText."
        }

        ReminderNotifier.notify(
            context = applicationContext,
            title = "Cobros próximos",
            message = message
        )

        prefs.edit()
            .putString("last_signature", signature)
            .putLong("last_time", now)
            .apply()

        return Result.success()
    }

    private fun daysUntilDayOfMonth(targetDay: Int): Int {
        val now = Calendar.getInstance()
        val today = now.get(Calendar.DAY_OF_MONTH)
        val maxDayCurrent = now.getActualMaximum(Calendar.DAY_OF_MONTH)
        val safeCurrentTarget = targetDay.coerceAtMost(maxDayCurrent)

        if (safeCurrentTarget >= today) {
            return safeCurrentTarget - today
        }

        val next = (now.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
        val maxDayNext = next.getActualMaximum(Calendar.DAY_OF_MONTH)
        val safeNextTarget = targetDay.coerceAtMost(maxDayNext)
        val remainingCurrent = maxDayCurrent - today
        return remainingCurrent + safeNextTarget
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "smart_reminder_daily_worker"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<SmartReminderWorker>(12, TimeUnit.HOURS)
                .setInitialDelay(1, TimeUnit.HOURS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }
    }
}

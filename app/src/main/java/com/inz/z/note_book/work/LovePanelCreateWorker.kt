package com.inz.z.note_book.work

import android.content.Context
import android.content.Intent
import androidx.work.*
import com.inz.z.base.util.L
import com.inz.z.note_book.util.Constants
import com.inz.z.note_book.view.activity.SplashActivity
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * LovePanel Worker.
 *
 * ====================================================
 * Create by 11654 in 2022/5/27 20:43
 */
class LovePanelCreateWorker(val context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    companion object {
        private const val TAG = "LovePanelCreateWorker"
    }

    override fun doWork(): Result {

        L.d(TAG, "doWork: START ---------->>> ")

        val intent = Intent()
        intent.action = Constants.AlarmAction.ALARM_BROADCAST_CLOCK_CREATE_LOVE_PANEL_ACTION
        intent.`package` = context.packageName
        context.sendBroadcast(intent)

        L.d(TAG, "doWork: SEND   --------->>> ")

        // 添加新 Work
        val currentCalendar = Calendar.getInstance(Locale.getDefault())
        val calendar = Calendar.getInstance(Locale.getDefault())
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        if (calendar.before(currentCalendar)) {
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 24)
        }
        val currentTime = System.currentTimeMillis()
        val difTime = calendar.timeInMillis - currentTime
        L.i(SplashActivity.TAG, "doWork: del time = $difTime ")
        val requestWork =
            OneTimeWorkRequestBuilder<LovePanelCreateWorker>()
                .setInitialDelay(difTime, TimeUnit.MILLISECONDS)
                .addTag("LOVE_PANEL_WORKER")
                .build()
        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork("CREATE_LOVE_PANEL", ExistingWorkPolicy.REPLACE, requestWork)

        return Result.success()
    }

}
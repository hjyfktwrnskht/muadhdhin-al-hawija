package com.muadhdhin.alhawija

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class AdhanScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val prayerTimesManager = PrayerTimesManager(context)
    private val dataStoreManager = DataStoreManager(context)

    companion object {
        private const val REQUEST_CODE_BASE = 1000
    }

    fun scheduleNextAdhan() {
        // إلغاء أي تنبيهات مجدولة مسبقًا
        cancelAllAlarms()

        // الحصول على مواقيت الصلاة لليوم
        val today = LocalDate.now()
        val todayTimes = prayerTimesManager.getTodayPrayerTimes(today)

        // الحصول على الصلاة القادمة
        val nextPrayer = prayerTimesManager.getNextPrayer(LocalTime.now(), today)

        if (nextPrayer != null) {
            val (prayerName, prayerTime) = nextPrayer
            val now = LocalDateTime.now()
            var nextAdhanDateTime = LocalDateTime.of(today, prayerTime)

            // إذا كانت الصلاة القادمة هي فجر اليوم التالي، يجب تعديل التاريخ
            if (prayerName == "Fajr" && prayerTime.isBefore(LocalTime.of(1, 0))) {
                // هذا يعني أن الفجر هو فجر اليوم التالي
                nextAdhanDateTime = LocalDateTime.of(today.plusDays(1), prayerTime)
            } else if (nextAdhanDateTime.isBefore(now)) {
                // إذا كان الوقت قد فات، يجب البحث عن الصلاة القادمة في اليوم التالي
                // ولكن دالة getNextPrayer تعالج هذا، لذا هذا الشرط قد لا يكون ضروريًا
                // ولكنه يضمن عدم جدولة تنبيه في الماضي
                val tomorrow = today.plusDays(1)
                val tomorrowTimes = prayerTimesManager.getTodayPrayerTimes(tomorrow)
                val tomorrowNextPrayer = prayerTimesManager.getNextPrayer(LocalTime.MIN, tomorrow)
                if (tomorrowNextPrayer != null) {
                    nextAdhanDateTime = LocalDateTime.of(tomorrow, tomorrowNextPrayer.second)
                } else {
                    Log.e("AdhanScheduler", "Could not find next prayer time for tomorrow.")
                    return
                }
            }

            val triggerTimeMillis = nextAdhanDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            // التأكد من أن الأذان ليس الفجر إذا كان معطلاً
            serviceScope.launch {
                dataStoreManager.fajrAdhanToggleFlow.collect { isFajrEnabled ->
                    if (prayerName.equals("Fajr", ignoreCase = true) && !isFajrEnabled) {
                        Log.d("AdhanScheduler", "Fajr adhan is disabled. Skipping scheduling.")
                        // البحث عن الصلاة التي تلي الفجر (عادةً الظهر)
                        val nextAfterFajr = prayerTimesManager.getNextPrayer(prayerTime.plusMinutes(1), nextAdhanDateTime.toLocalDate())
                        if (nextAfterFajr != null) {
                            scheduleAdhan(nextAfterFajr.first, nextAfterFajr.second, nextAdhanDateTime.toLocalDate())
                        }
                        return@collect
                    }

                    scheduleAdhan(prayerName, prayerTime, nextAdhanDateTime.toLocalDate())
                }
            }

            Log.i("AdhanScheduler", "Scheduled next adhan for $prayerName at $nextAdhanDateTime")
        } else {
            Log.e("AdhanScheduler", "Could not find next prayer time.")
        }
    }

    private fun scheduleAdhan(prayerName: String, prayerTime: LocalTime, date: LocalDate) {
        val nextAdhanDateTime = LocalDateTime.of(date, prayerTime)
        val triggerTimeMillis = nextAdhanDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val intent = Intent(context, AdhanService::class.java).apply {
            action = "ACTION_PLAY_ADHAN"
            // يمكن إضافة معلومات الصلاة هنا إذا لزم الأمر
        }

        val pendingIntent = PendingIntent.getService(
            context,
            REQUEST_CODE_BASE + prayerName.hashCode(), // استخدام كود طلب فريد لكل صلاة
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // جدولة التنبيه الدقيق
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            } else {
                // إذا لم يكن مسموحًا بجدولة التنبيهات الدقيقة، نستخدم setAndAllowWhileIdle
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMillis,
                    pendingIntent
                )
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMillis,
                pendingIntent
            )
        }
    }

    fun cancelAllAlarms() {
        // بما أننا لا نعرف أسماء الصلوات المجدولة، سنحاول إلغاء التنبيهات باستخدام نفس الـ Intent
        // هذا ليس مثاليًا ولكنه يعمل إذا كنا نجدول تنبيهًا واحدًا فقط في كل مرة
        val intent = Intent(context, AdhanService::class.java).apply {
            action = "ACTION_PLAY_ADHAN"
        }
        val pendingIntent = PendingIntent.getService(
            context,
            REQUEST_CODE_BASE, // استخدام كود أساسي للإلغاء
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.let {
            alarmManager.cancel(it)
            Log.d("AdhanScheduler", "Canceled existing adhan alarm.")
        }
    }
}

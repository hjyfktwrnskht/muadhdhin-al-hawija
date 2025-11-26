package com.muadhdhin.alhawija

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Timer
import java.util.TimerTask

/**
 * تنفيذ الـ App Widget لعرض الوقت المتبقي للصلاة القادمة واسمها.
 */
class AdhanAppWidgetProvider : AppWidgetProvider() {

    private val prayerTimesManager by lazy { PrayerTimesManager(context) }
    private lateinit var context: Context

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        this.context = context
        // هناك عدة ويدجت، قم بتحديثها جميعًا
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // أدخل هنا أي منطق عند إضافة أول ويدجت
    }

    override fun onDisabled(context: Context) {
        // أدخل هنا أي منطق عند إزالة آخر ويدجت
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.adhan_app_widget)

        // الحصول على الصلاة القادمة والوقت المتبقي
        val nextPrayer = prayerTimesManager.getNextPrayer(LocalTime.now())
        if (nextPrayer != null) {
            val (name, time) = nextPrayer
            val now = LocalTime.now()
            val duration = java.time.Duration.between(now, time)

            val hours = duration.toHours()
            val minutes = duration.toMinutes() % 60
            val countdownText = String.format("%02d:%02d", hours, minutes)

            views.setTextViewText(R.id.tv_next_prayer_name_widget, name)
            views.setTextViewText(R.id.tv_countdown_widget, countdownText)
        } else {
            views.setTextViewText(R.id.tv_next_prayer_name_widget, "لا توجد صلاة قادمة")
            views.setTextViewText(R.id.tv_countdown_widget, "--:--")
        }

        // إنشاء PendingIntent لفتح التطبيق عند النقر على الـ Widget
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent)

        // إبلاغ AppWidgetManager لتحديث الـ Widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

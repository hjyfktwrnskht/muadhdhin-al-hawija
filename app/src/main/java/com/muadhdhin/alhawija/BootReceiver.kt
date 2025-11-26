package com.muadhdhin.alhawija

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            Log.d("BootReceiver", "Boot completed. Starting AdhanService.")
            val serviceIntent = Intent(context, AdhanService::class.java).apply {
                action = AdhanService.ACTION_START_FOREGROUND_SERVICE
            }
            // بدء الخدمة في المقدمة
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
            // يجب إعادة جدولة مواقيت الصلاة هنا
            // سيتم تنفيذ منطق الجدولة في مرحلة لاحقة
        }
    }
}

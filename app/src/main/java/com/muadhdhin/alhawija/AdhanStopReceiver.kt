package com.muadhdhin.alhawija

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AdhanStopReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AdhanService.ACTION_STOP_ADHAN) {
            Log.d("AdhanStopReceiver", "Received stop adhan broadcast.")
            // إرسال أمر إلى الخدمة لإيقاف الأذان
            val serviceIntent = Intent(context, AdhanService::class.java).apply {
                action = AdhanService.ACTION_STOP_ADHAN
            }
            context.startService(serviceIntent)
        }
    }
}

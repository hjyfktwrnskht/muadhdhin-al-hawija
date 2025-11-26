package com.muadhdhin.alhawija

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AdhanService : Service() {

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private lateinit var dataStoreManager: DataStoreManager
    private var mediaPlayer: MediaPlayer? = null
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        const val NOTIFICATION_ID = 101
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
        const val ACTION_STOP_ADHAN = "ACTION_STOP_ADHAN"
        const val EXTRA_ADHAN_SOUND_INDEX = "EXTRA_ADHAN_SOUND_INDEX"
    }

    override fun onCreate() {
        super.onCreate()
        dataStoreManager = DataStoreManager(applicationContext)
        createNotificationChannel()
        acquireWakeLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_FOREGROUND_SERVICE -> {
                startForeground(NOTIFICATION_ID, buildNotification())
                Log.d("AdhanService", "Foreground Service Started")
            }
            ACTION_STOP_FOREGROUND_SERVICE -> {
                stopForeground(true)
                stopSelf()
                Log.d("AdhanService", "Foreground Service Stopped")
            }
            ACTION_STOP_ADHAN -> {
                stopAdhan()
                Log.d("AdhanService", "Adhan Stopped via Notification Action")
            }
            // تشغيل الأذان عند وصول تنبيه من AlarmManager
            "ACTION_PLAY_ADHAN" -> {
                val adhanIndex = intent.getIntExtra(EXTRA_ADHAN_SOUND_INDEX, 1)
                playAdhan(adhanIndex)
            }
        }
        // يجب أن تعود START_STICKY لضمان إعادة تشغيل الخدمة إذا تم إيقافها بواسطة النظام
        return START_STICKY
    }

    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "MuadhdhinAlHawija::AdhanWakeLock"
        )
        wakeLock?.acquire(10 * 60 * 1000L /* 10 minutes */) // الحصول على WakeLock
    }

    private fun releaseWakeLock() {
        if (wakeLock?.isHeld == true) {
            wakeLock?.release()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_LOW // استخدام أهمية منخفضة للإشعار الدائم
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // زر إيقاف الأذان
        val stopAdhanIntent = Intent(this, AdhanStopReceiver::class.java).apply {
            action = ACTION_STOP_ADHAN
        }
        val stopAdhanPendingIntent = PendingIntent.getBroadcast(
            this, 0, stopAdhanIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stopAdhanAction = NotificationCompat.Action.Builder(
            R.drawable.ic_stop, // يجب إضافة أيقونة إيقاف
            getString(R.string.stop_adhan),
            stopAdhanPendingIntent
        ).build()

        return NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_text))
            .setSmallIcon(R.drawable.ic_adhan_notification) // يجب إضافة أيقونة إشعار
            .setContentIntent(pendingIntent)
            .setOngoing(true) // إشعار دائم
            .addAction(stopAdhanAction)
            .build()
    }

    private fun playAdhan(adhanIndex: Int) {
        serviceScope.launch {
            // إيقاف أي أذان سابق
            stopAdhan()

            // تحديد ملف الأذان
            val resourceId = when (adhanIndex) {
                1 -> R.raw.adhan1
                2 -> R.raw.adhan2
                3 -> R.raw.adhan3
                else -> R.raw.adhan1
            }

            // الحصول على درجة الصوت من الإعدادات
            dataStoreManager.volumeFlow.collect { volume ->
                val volumeLevel = volume / 100f
                mediaPlayer = MediaPlayer.create(applicationContext, resourceId)?.apply {
                    setVolume(volumeLevel, volumeLevel)
                    setOnCompletionListener {
                        stopAdhan()
                    }
                    start()
                }
            }
        }
    }

    private fun stopAdhan() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
            mediaPlayer = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAdhan()
        releaseWakeLock()
        serviceJob.cancel()
        Log.d("AdhanService", "Service Destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}

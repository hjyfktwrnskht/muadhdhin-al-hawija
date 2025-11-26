package com.muadhdhin.alhawija

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.muadhdhin.alhawija.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adhanScheduler: AdhanScheduler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adhanScheduler = AdhanScheduler(applicationContext)

        // 1. بدء خدمة Foreground Service
        startAdhanService()

        // 2. طلب الأذونات اللازمة
        requestPermissions()

        // 3. جدولة الأذان
        adhanScheduler.scheduleNextAdhan()

        // 4. طلب تجاهل تحسينات البطارية
        requestIgnoreBatteryOptimizations()

        // 5. إعداد واجهة المستخدم (سيتم تطويرها في المرحلة التالية)
        setupUI()
    }

    private fun startAdhanService() {
        val serviceIntent = Intent(this, AdhanService::class.java).apply {
            action = AdhanService.ACTION_START_FOREGROUND_SERVICE
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(this, serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    private fun requestPermissions() {
        // طلب إذن الإشعارات (لأندرويد 13 فما فوق)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
            }
        }
        // طلب إذن Exact Alarms (لأندرويد 12 فما فوق)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as android.app.AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
            }
        }
    }

    private fun requestIgnoreBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val packageName = packageName
            val pm = getSystemService(POWER_SERVICE) as android.os.PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = android.net.Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
    }

    private fun setupUI() {
        // سيتم تطوير واجهة المستخدم هنا في المرحلة 4
        binding.textView.text = "مؤذّن الحويجة - قيد التطوير"
    }
}

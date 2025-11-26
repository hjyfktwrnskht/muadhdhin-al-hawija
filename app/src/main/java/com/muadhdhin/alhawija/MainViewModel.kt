package com.muadhdhin.alhawija

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prayerTimesManager = PrayerTimesManager(application)
    private val dataStoreManager = DataStoreManager(application)
    private var timerJob: Job? = null

    private val _currentTime = MutableLiveData<String>()
    val currentTime: LiveData<String> = _currentTime

    private val _hijriDate = MutableLiveData<String>()
    val hijriDate: LiveData<String> = _hijriDate

    private val _nextPrayerName = MutableLiveData<String>()
    val nextPrayerName: LiveData<String> = _nextPrayerName

    private val _countdown = MutableLiveData<String>()
    val countdown: LiveData<String> = _countdown

    private var nextPrayerTime: LocalTime? = null
    private var isAdhanPlaying = false

    init {
        startTimer()
        // يجب أن يتم حساب التاريخ الهجري بشكل صحيح، لكن لتبسيط المثال سأستخدم تاريخ ميلادي مؤقت
        _hijriDate.value = "15 ربيع الأول 1447 هـ"
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val now = LocalTime.now()
                _currentTime.postValue(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                updatePrayerTimesInfo(now)
                delay(1000)
            }
        }
    }

    private fun updatePrayerTimesInfo(currentTime: LocalTime) {
        val nextPrayer = prayerTimesManager.getNextPrayer(currentTime)

        if (nextPrayer != null) {
            val (name, time) = nextPrayer
            nextPrayerTime = time
            _nextPrayerName.postValue(name)

            val nowDateTime = LocalDateTime.now()
            val nextPrayerDateTime = LocalDateTime.of(LocalDate.now(), time)

            var duration = Duration.between(nowDateTime, nextPrayerDateTime)

            // منطق العد التنازلي/التصاعدي بعد الأذان
            if (duration.isNegative) {
                // الصلاة قد بدأت، نحسب الوقت المنقضي
                val elapsedDuration = duration.abs()
                val elapsedMinutes = elapsedDuration.toMinutes()

                if (elapsedMinutes <= 30) {
                    // إذا كان الوقت المنقضي أقل من أو يساوي 30 دقيقة، نعرضه بالسالب
                    val seconds = elapsedDuration.seconds % 60
                    val minutes = elapsedDuration.toMinutes() % 60
                    val hours = elapsedDuration.toHours()
                    _countdown.postValue(String.format(Locale.getDefault(), "-%02d:%02d:%02d", hours, minutes, seconds))
                } else {
                    // بعد 30 دقيقة، نبحث عن الصلاة التالية
                    val nextNextPrayer = prayerTimesManager.getNextPrayer(currentTime.plusMinutes(1))
                    if (nextNextPrayer != null) {
                        val (nextName, nextTime) = nextNextPrayer
                        nextPrayerTime = nextTime
                        _nextPrayerName.postValue(nextName)
                        duration = Duration.between(nowDateTime, LocalDateTime.of(LocalDate.now(), nextTime))
                        updateCountdownText(duration)
                    } else {
                        _countdown.postValue("---")
                    }
                }
            } else {
                // الوقت المتبقي للصلاة القادمة
                updateCountdownText(duration)
            }
        } else {
            _nextPrayerName.postValue("لا توجد صلوات متبقية اليوم")
            _countdown.postValue("---")
        }
    }

    private fun updateCountdownText(duration: Duration) {
        val seconds = duration.seconds % 60
        val minutes = duration.toMinutes() % 60
        val hours = duration.toHours()
        _countdown.postValue(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds))
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

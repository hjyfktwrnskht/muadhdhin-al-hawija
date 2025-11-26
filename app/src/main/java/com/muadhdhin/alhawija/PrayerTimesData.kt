package com.muadhdhin.alhawija

import android.content.Context
import android.util.Log
import com.opencsv.CSVReader
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class PrayerTime(
    val month: String,
    val day: Int,
    val fajrFirst: LocalTime,
    val fajrSecond: LocalTime,
    val sunrise: LocalTime,
    val dhuhr: LocalTime,
    val asr: LocalTime,
    val maghrib: LocalTime,
    val isha: LocalTime
) {
    // دالة مساعدة للحصول على وقت الصلاة حسب اسمها
    fun getTimeForPrayer(prayerName: String): LocalTime? {
        return when (prayerName.lowercase(Locale.ROOT)) {
            "fajr" -> fajrSecond // استخدام الفجر الثاني كما هو شائع للأذان
            "sunrise" -> sunrise
            "dhuhr" -> dhuhr
            "asr" -> asr
            "maghrib" -> maghrib
            "isha" -> isha
            else -> null
        }
    }
}

class PrayerTimesManager(private val context: Context) {

    private val allPrayerTimes = mutableListOf<PrayerTime>()
    private val timeFormatter = DateTimeFormatter.ofPattern("H:mm")

    init {
        loadPrayerTimes()
    }

    private fun loadPrayerTimes() {
        try {
            val inputStream = context.assets.open("prayer_times.csv")
            val reader = CSVReader(InputStreamReader(inputStream))
            // تخطي سطر الرأس
            reader.readNext()

            var line: Array<String>?
            while (reader.readNext().also { line = it } != null) {
                if (line!!.size >= 9) {
                    try {
                        val prayerTime = PrayerTime(
                            month = line!![0],
                            day = line!![1].toInt(),
                            fajrFirst = LocalTime.parse(line!![2], timeFormatter),
                            fajrSecond = LocalTime.parse(line!![3], timeFormatter),
                            sunrise = LocalTime.parse(line!![4], timeFormatter),
                            dhuhr = LocalTime.parse(line!![5], timeFormatter),
                            asr = LocalTime.parse(line!![6], timeFormatter),
                            maghrib = LocalTime.parse(line!![7], timeFormatter),
                            isha = LocalTime.parse(line!![8], timeFormatter)
                        )
                        allPrayerTimes.add(prayerTime)
                    } catch (e: Exception) {
                        Log.e("PrayerTimesManager", "Error parsing line: ${line!!.joinToString()}, Error: ${e.message}")
                    }
                }
            }
            reader.close()
            Log.d("PrayerTimesManager", "Loaded ${allPrayerTimes.size} prayer times.")
        } catch (e: Exception) {
            Log.e("PrayerTimesManager", "Error loading prayer times from CSV: ${e.message}")
        }
    }

    fun getTodayPrayerTimes(date: LocalDate = LocalDate.now()): PrayerTime? {
        // بما أن الملف لا يحتوي على السنة، سنعتمد على الشهر واليوم فقط
        val monthName = getMonthName(date.monthValue)
        val dayOfMonth = date.dayOfMonth

        return allPrayerTimes.find {
            it.month == monthName && it.day == dayOfMonth
        }
    }

    fun getNextPrayer(currentTime: LocalTime = LocalTime.now(), date: LocalDate = LocalDate.now()): Pair<String, LocalTime>? {
        val todayTimes = getTodayPrayerTimes(date) ?: return null

        val prayers = listOf(
            "Fajr" to todayTimes.fajrSecond,
            "Sunrise" to todayTimes.sunrise,
            "Dhuhr" to todayTimes.dhuhr,
            "Asr" to todayTimes.asr,
            "Maghrib" to todayTimes.maghrib,
            "Isha" to todayTimes.isha
        )

        // البحث عن الصلاة القادمة في نفس اليوم
        for ((name, time) in prayers) {
            if (time.isAfter(currentTime)) {
                return name to time
            }
        }

        // إذا لم يكن هناك صلاة متبقية اليوم، تكون الصلاة القادمة هي فجر اليوم التالي
        val tomorrowTimes = getTodayPrayerTimes(date.plusDays(1))
        return if (tomorrowTimes != null) {
            "Fajr" to tomorrowTimes.fajrSecond
        } else {
            null
        }
    }

    private fun getMonthName(monthValue: Int): String {
        return when (monthValue) {
            1 -> "كانون الثاني"
            2 -> "شباط"
            3 -> "آذار"
            4 -> "نيسان"
            5 -> "أيار"
            6 -> "حزيران"
            7 -> "تموز"
            8 -> "آب"
            9 -> "أيلول"
            10 -> "تشرين الأول"
            11 -> "تشرين الثاني"
            12 -> "كانون الأول"
            else -> ""
        }
    }
}

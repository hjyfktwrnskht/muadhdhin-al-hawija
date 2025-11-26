package com.muadhdhin.alhawija

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// اسم ملف DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(context: Context) {

    private val dataStore = context.dataStore

    // مفاتيح الإعدادات
    companion object {
        val ADHAN_SOUND_KEY = intPreferencesKey("adhan_sound_index") // 1, 2, or 3
        val FAJR_ADHAN_TOGGLE_KEY = booleanPreferencesKey("fajr_adhan_enabled")
        val VOLUME_KEY = intPreferencesKey("adhan_volume") // 0 to 100
        val NIGHT_MODE_KEY = booleanPreferencesKey("night_mode_enabled")
    }

    // 1. حفظ واختيار صوت المؤذن
    suspend fun saveAdhanSound(index: Int) {
        dataStore.edit { settings ->
            settings[ADHAN_SOUND_KEY] = index
        }
    }

    // قراءة صوت المؤذن المختار
    val adhanSoundFlow: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[ADHAN_SOUND_KEY] ?: 1 // القيمة الافتراضية هي الصوت الأول
        }

    // 2. حفظ حالة تفعيل/تعطيل أذان الفجر
    suspend fun saveFajrAdhanToggle(isEnabled: Boolean) {
        dataStore.edit { settings ->
            settings[FAJR_ADHAN_TOGGLE_KEY] = isEnabled
        }
    }

    // قراءة حالة تفعيل/تعطيل أذان الفجر
    val fajrAdhanToggleFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[FAJR_ADHAN_TOGGLE_KEY] ?: true // القيمة الافتراضية هي مفعل
        }

    // 3. حفظ درجة الصوت
    suspend fun saveVolume(volume: Int) {
        dataStore.edit { settings ->
            settings[VOLUME_KEY] = volume
        }
    }

    // قراءة درجة الصوت
    val volumeFlow: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[VOLUME_KEY] ?: 80 // القيمة الافتراضية 80%
        }

    // 4. حفظ حالة الوضع الليلي
    suspend fun saveNightMode(isEnabled: Boolean) {
        dataStore.edit { settings ->
            settings[NIGHT_MODE_KEY] = isEnabled
        }
    }

    // قراءة حالة الوضع الليلي
    val nightModeFlow: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[NIGHT_MODE_KEY] ?: false // القيمة الافتراضية هي معطل
        }
}

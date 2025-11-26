# مؤذّن الحويجة - تطبيق أذان أندرويد

تطبيق أذان متقدم لنظام أندرويد يعمل بخدمة Foreground Service في الخلفية، مع ثلاثة أصوات أذان قابلة للاختيار، ومواقيت صلاة دقيقة، وواجهة مستخدم بتصميم إسلامي حديث.

## المميزات الرئيسية

### 1. العمل في الخلفية
- **Foreground Service**: يعمل التطبيق بخدمة Foreground Service لضمان استمرار عمله حتى عند إغلاق التطبيق أو قفل الجهاز
- **WakeLock**: استخدام WakeLock لمنع دخول الجهاز في وضع السكون أثناء تشغيل الأذان
- **AlarmManager**: جدولة دقيقة للأذان باستخدام AlarmManager مع دعم Exact Alarms

### 2. أصوات الأذان
- **ثلاثة أصوات مختلفة**: يمكن للمستخدم اختيار من بين 3 أصوات أذان مختلفة
- **تخزين محلي**: جميع الأصوات مخزنة محليًا في التطبيق (adhan1.mp3, adhan2.mp3, adhan3.mp3)
- **التحكم بالصوت**: التحكم الكامل بدرجة صوت الأذان

### 3. مواقيت الصلاة
- **بيانات دقيقة**: مواقيت صلاة دقيقة لكل يوم من أيام السنة
- **تحديث تلقائي**: تحديث يومي تلقائي للمواقيت
- **حساب الوقت المتبقي**: عداد تنازلي يعرض الوقت المتبقي للصلاة القادمة
- **عداد سالب بعد الأذان**: بعد الأذان يحسب الوقت المنقضي بالسالب لمدة 30 دقيقة فقط

### 4. الإشعارات
- **إشعار دائم**: إشعار دائم في شريط الإشعارات يعرض الصلاة القادمة والوقت المتبقي
- **زر إيقاف الأذان**: يمكن إيقاف الأذان مباشرة من الإشعار
- **إشعارات الأذان**: إشعارات عند وقت الأذان

### 5. الواجهة الرسومية
- **تصميم إسلامي حديث**: ألوان أخضر داكن وذهبي وأبيض
- **شاشة رئيسية**: عرض الوقت الحالي والصلاة القادمة والعداد التنازلي والتاريخ الهجري
- **شاشة مواقيت الصلاة**: عرض جميع مواقيت الصلاة لليوم الحالي
- **شاشة اختيار المؤذن**: اختيار صوت الأذان المفضل
- **شاشة الإعدادات**: التحكم بجميع إعدادات التطبيق

### 6. الإعدادات
- **تفعيل/تعطيل أذان الفجر**: يمكن تعطيل أذان الفجر إذا أراد المستخدم
- **التحكم بدرجة الصوت**: شريط تمرير للتحكم بدرجة صوت الأذان (0-100%)
- **الوضع الليلي**: تفعيل/تعطيل الوضع الليلي
- **إعدادات الإشعارات**: التحكم بالإشعارات من خلال إعدادات النظام
- **إعدادات البطارية**: تجاهل تحسينات البطارية لضمان استمرار عمل التطبيق

### 7. Widget
- **Widget للشاشة الرئيسية**: عرض الصلاة القادمة والوقت المتبقي على الشاشة الرئيسية
- **تحديث دوري**: يتم تحديث Widget كل 30 دقيقة

## الأذونات المطلوبة

```xml
<!-- العمل في الخلفية -->
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

<!-- الإشعارات -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

<!-- جدولة التنبيهات الدقيقة -->
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
<uses-permission android:name="android.permission.USE_EXACT_ALARM" />

<!-- تجاهل تحسينات البطارية -->
<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
```

## متطلبات البناء

- **Android SDK**: API 24 (Android 7.0) أو أعلى
- **Gradle**: 8.2 أو أعلى
- **Java**: JDK 1.8 أو أعلى
- **Kotlin**: 1.9.22 أو أعلى

## الاعتماديات الرئيسية

```gradle
// Core Android
androidx.core:core-ktx:1.12.0
androidx.appcompat:appcompat:1.6.1

// Material Design
com.google.android.material:material:1.11.0

// Constraints Layout
androidx.constraintlayout:constraintlayout:2.1.4

// Lifecycle
androidx.lifecycle:lifecycle-service:2.7.0

// Work Manager
androidx.work:work-runtime-ktx:2.9.0

// CSV Reading
com.opencsv:opencsv:5.9

// Coroutines
org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3
org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3

// DataStore
androidx.datastore:datastore-preferences:1.0.0
```

## هيكل المشروع

```
muadhdhin_al_hawija/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/muadhdhin/alhawija/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── MainViewModel.kt
│   │   │   │   ├── AdhanService.kt
│   │   │   │   ├── AdhanScheduler.kt
│   │   │   │   ├── AdhanStopReceiver.kt
│   │   │   │   ├── BootReceiver.kt
│   │   │   │   ├── DataStoreManager.kt
│   │   │   │   ├── PrayerTimesData.kt
│   │   │   │   ├── AdhanAppWidgetProvider.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── main/MainFragment.kt
│   │   │   │   │   ├── prayertimes/PrayerTimesFragment.kt
│   │   │   │   │   └── settings/
│   │   │   │   │       ├── SettingsFragment.kt
│   │   │   │   │       └── MuadhdhinSelectionFragment.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── fragment_main.xml
│   │   │   │   │   ├── fragment_prayer_times.xml
│   │   │   │   │   ├── fragment_muadhdhin_selection.xml
│   │   │   │   │   ├── fragment_settings.xml
│   │   │   │   │   ├── item_prayer_time.xml
│   │   │   │   │   └── adhan_app_widget.xml
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── circle_button_background.xml
│   │   │   │   │   ├── ic_settings.xml
│   │   │   │   │   ├── ic_prayer_times.xml
│   │   │   │   │   ├── ic_adhan_notification.xml
│   │   │   │   │   ├── ic_stop.xml
│   │   │   │   │   └── widget_background.xml
│   │   │   │   ├── values/
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   ├── xml/
│   │   │   │   │   └── adhan_app_widget_info.xml
│   │   │   │   └── raw/
│   │   │   │       ├── adhan1.mp3
│   │   │   │       ├── adhan2.mp3
│   │   │   │       └── adhan3.mp3
│   │   │   ├── assets/
│   │   │   │   └── prayer_times.csv
│   │   │   └── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
└── README.md
```

## خطوات البناء

### 1. باستخدام Android Studio
1. افتح Android Studio
2. انقر على "Open" واختر مجلد المشروع
3. انتظر حتى يتم تحميل المشروع بالكامل
4. انقر على "Build" > "Build Bundle(s) / APK(s)" > "Build APK(s)"
5. سيتم حفظ ملف APK في `app/build/outputs/apk/debug/`

### 2. باستخدام سطر الأوامر
```bash
cd muadhdhin_al_hawija
./gradlew assembleDebug
```

ملف APK سيكون في: `app/build/outputs/apk/debug/app-debug.apk`

### 3. بناء Release APK
```bash
./gradlew assembleRelease
```

ملف APK سيكون في: `app/build/outputs/apk/release/app-release.apk`

## التثبيت على الجهاز

### باستخدام ADB
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### باستخدام Android Studio
1. قم بتوصيل الجهاز عبر USB
2. انقر على "Run" > "Run 'app'"

## الحقوق

© 2025 – جميع الحقوق محفوظة
عبدالرحمن الجميلي

## ملاحظات مهمة

1. **مواقيت الصلاة**: يجب تحديث ملف `prayer_times.csv` سنويًا بمواقيت الصلاة الجديدة
2. **الأصوات**: يمكن استبدال ملفات الأصوات (adhan1.mp3, adhan2.mp3, adhan3.mp3) بأصوات أخرى
3. **التاريخ الهجري**: يتم حساب التاريخ الهجري بشكل مؤقت ويمكن تحسينه لاحقًا
4. **الأذونات**: تأكد من منح التطبيق جميع الأذونات المطلوبة عند التشغيل الأول

## الدعم والمساعدة

للمزيد من المعلومات أو الإبلاغ عن مشاكل، يرجى التواصل مع المطور.

# تعليمات بناء وتثبيت تطبيق مؤذّن الحويجة

هذا الملف يحتوي على تعليمات مفصلة لبناء وتثبيت تطبيق مؤذّن الحويجة على جهازك.

## المتطلبات الأساسية

قبل البدء، تأكد من توفر ما يلي:

1. **Android Studio** (الإصدار الأخير)
   - تحميل من: https://developer.android.com/studio

2. **Android SDK**
   - SDK API Level 34 (Android 14)
   - SDK Tools
   - Build Tools 34.0.0 أو أعلى

3. **Java Development Kit (JDK)**
   - JDK 11 أو أعلى
   - يمكن تثبيته من خلال Android Studio

4. **جهاز أندرويد أو محاكي**
   - جهاز فعلي بـ Android 7.0 (API 24) أو أعلى
   - أو محاكي Android من خلال Android Studio

## خطوات البناء والتثبيت

### الطريقة الأولى: باستخدام Android Studio (الموصى به)

#### 1. فتح المشروع
```bash
1. افتح Android Studio
2. انقر على "File" > "Open"
3. اختر مجلد "muadhdhin_al_hawija"
4. انقر على "Open"
```

#### 2. انتظر تحميل المشروع
- سيقوم Android Studio بتحميل جميع الملفات والاعتماديات
- قد يستغرق هذا عدة دقائق في المرة الأولى
- تأكد من اتصالك بالإنترنت

#### 3. تثبيت الاعتماديات
- إذا طُلب منك تحميل أي مكونات إضافية، انقر على "Install"
- انتظر حتى ينتهي التحميل

#### 4. بناء ملف APK
```
انقر على "Build" في الشريط العلوي
اختر "Build Bundle(s) / APK(s)"
اختر "Build APK(s)"
```

#### 5. انتظر انتهاء البناء
- سيظهر إشعار عند انتهاء البناء
- سيتم حفظ ملف APK في: `app/build/outputs/apk/debug/app-debug.apk`

#### 6. تثبيت التطبيق على الجهاز
```
توصيل جهاز أندرويد عبر USB (أو استخدام محاكي)
انقر على "Run" > "Run 'app'"
اختر الجهاز المراد التثبيت عليه
انقر على "OK"
```

### الطريقة الثانية: باستخدام سطر الأوامر

#### 1. فتح Terminal/Command Prompt
```bash
cd /path/to/muadhdhin_al_hawija
```

#### 2. بناء ملف APK للتطوير (Debug)
```bash
./gradlew assembleDebug
```

ملف APK سيكون في:
```
app/build/outputs/apk/debug/app-debug.apk
```

#### 3. بناء ملف APK للإطلاق (Release)
```bash
./gradlew assembleRelease
```

ملف APK سيكون في:
```
app/build/outputs/apk/release/app-release.apk
```

#### 4. تثبيت التطبيق على الجهاز
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### الطريقة الثالثة: بناء ملف APK للتوزيع

#### 1. إنشاء Keystore (مفتاح التوقيع)
```bash
keytool -genkey -v -keystore muadhdhin.keystore -keyalg RSA -keysize 2048 -validity 10000 -alias muadhdhin
```

سيطلب منك:
- كلمة مرور Keystore
- معلومات شخصية (الاسم، الدولة، إلخ)

#### 2. بناء ملف APK موقع
قم بتعديل ملف `build.gradle` في مجلد `app`:

```gradle
android {
    ...
    signingConfigs {
        release {
            storeFile file("../muadhdhin.keystore")
            storePassword "your_password"
            keyAlias "muadhdhin"
            keyPassword "your_password"
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

#### 3. بناء ملف APK الموقع
```bash
./gradlew assembleRelease
```

ملف APK النهائي سيكون في:
```
app/build/outputs/apk/release/app-release.apk
```

## استكشاف الأخطاء

### مشكلة: "Gradle sync failed"
**الحل:**
1. تأكد من اتصالك بالإنترنت
2. انقر على "File" > "Sync Now"
3. إذا استمرت المشكلة، حاول حذف مجلد `.gradle` وإعادة المحاولة

### مشكلة: "Android SDK not found"
**الحل:**
1. افتح Android Studio
2. انقر على "Tools" > "SDK Manager"
3. تأكد من تثبيت SDK API 34 وأدوات البناء

### مشكلة: "Build failed"
**الحل:**
1. تأكد من تثبيت جميع الاعتماديات
2. حاول تنظيف المشروع: `./gradlew clean`
3. ثم حاول البناء مرة أخرى: `./gradlew assembleDebug`

### مشكلة: "Device not found"
**الحل:**
1. تأكد من توصيل الجهاز عبر USB
2. فعّل وضع المطور على الجهاز:
   - اذهب إلى "Settings" > "About phone"
   - انقر على "Build number" 7 مرات
   - اذهب إلى "Settings" > "Developer options"
   - فعّل "USB Debugging"
3. في Command Prompt، اكتب: `adb devices`
4. تأكد من ظهور جهازك في القائمة

## بعد التثبيت

### الخطوات الأولى
1. افتح التطبيق
2. امنح التطبيق الأذونات المطلوبة:
   - إذن الإشعارات
   - إذن جدولة التنبيهات الدقيقة
   - إذن تجاهل تحسينات البطارية
3. اذهب إلى الإعدادات واختر صوت الأذان المفضل

### تفعيل الـ Widget
1. اضغط لفترة طويلة على الشاشة الرئيسية
2. اختر "Widgets"
3. ابحث عن "مؤذّن الحويجة"
4. اسحب الـ Widget إلى الشاشة الرئيسية

## نصائح مهمة

1. **استمرار العمل في الخلفية**
   - تأكد من عدم إزالة التطبيق من الذاكرة الحية (Swipe up)
   - امنح التطبيق إذن تجاهل تحسينات البطارية

2. **دقة مواقيت الصلاة**
   - تأكد من ضبط الوقت على جهازك بشكل صحيح
   - استخدم "Set time automatically" في إعدادات النظام

3. **جودة الصوت**
   - تأكد من أن صوت الجهاز مفعل
   - اختبر صوت الأذان من خلال الإعدادات

## الدعم

إذا واجهت أي مشاكل، يرجى:
1. التحقق من ملف README.md
2. مراجعة قسم استكشاف الأخطاء أعلاه
3. التواصل مع المطور

---

**ملاحظة:** هذا التطبيق تم تطويره لأغراض تعليمية وقد يحتاج إلى تحسينات إضافية للاستخدام الإنتاجي.

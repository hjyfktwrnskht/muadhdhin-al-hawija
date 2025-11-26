# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html

-dontusemixedcaseclassnames
# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
-dontwarn com.google.gson.**

# Application classes that will be serialized/deserialized over Gson
-keep class com.muadhdhin.alhawija.** { *; }

# Prevent renaming of native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep custom application classes
-keep class com.muadhdhin.alhawija.** { *; }
-keep class androidx.** { *; }
-keep class com.google.android.material.** { *; }

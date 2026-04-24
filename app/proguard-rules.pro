# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /usr/local/android-sdk/tools/proguard/proguard-android.txt

# Keep Room entities
-keep class com.telugulingo.app.data.local.db.entity.** { *; }

# Keep Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }

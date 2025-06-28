# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Plinio\AppData\Local\Android\sdk\tools\proguard\proguard-android-optimize.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ProGuard rule for classes annotated with @Keep.
-keep @androidx.annotation.Keep class * {*;}

# Hilt
-keep class * implements dagger.hilt.internal.GeneratedEntryPoint { <init>(); }
-keep class com.cebolarekords.player.MainActivity { *; }
-keep class * implements dagger.hilt.internal.GeneratedComponentManager { <init>(); }
-keep class * implements dagger.hilt.internal.GeneratedComponentManagerHolder { <init>(); }
-keep @dagger.hilt.android.HiltAndroidApp class * { <init>(); }
-keep @dagger.hilt.EntryPoint class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }
-keep @dagger.hilt.codegen.OriginatingElement class * { *; }
-keep @dagger.Module class * { *; }
-keep @dagger.Provides interface *
-keep @javax.inject.Inject class * { *; }
-keep @javax.inject.Inject interface * { *; }
-keep @javax.inject.Singleton class * { *; }
-keepclassmembers class * { @javax.inject.Inject <init>(...); }
-keepclassmembers class * { @dagger.hilt.android.internal.lifecycle.HiltViewModelFactory *; }

# Manter nossos modelos de dados (Data Classes)
-keep class com.cebolarekords.player.data.** { *; }
# Regras Padrão do Android e Kotlin
-keep class kotlin.coroutines.jvm.internal.BaseContinuationImpl
-dontwarn kotlin.collections.List
-dontwarn kotlin.collections.Map
-dontwarn kotlin.collections.Set

# Hilt
-keep class * implements dagger.hilt.internal.GeneratedEntryPoint
-keep class * implements dagger.hilt.internal.GeneratedComponentManager
-keep class * implements dagger.hilt.internal.GeneratedComponentManagerHolder
-keep @dagger.hilt.android.HiltAndroidApp class * {*;}
-keep @dagger.Module class * {*;}
-keep @dagger.hilt.InstallIn class * {*;}
-keep class **_HiltModules* {*;}
-keep class **_HiltComponents* {*;}
-keep class Dagger*Component {*;}
-keep @javax.inject.Inject class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }

# Room
-keep class androidx.room.** { *; }
-keepclassmembers class **.database.*Dao { *; }
-keepclassmembers class **.database.*Entity { *; }

# Media3 (ExoPlayer)
-keepnames class androidx.media3.common.Player$Listener
-keepnames class androidx.media3.common.SimpleBasePlayer
-keepnames class androidx.media3.exoplayer.ExoPlayer
-keepnames class androidx.media3.session.MediaSessionService
-keep public class androidx.media3.session.MediaButtonReceiver

# Jetpack Compose
-keepclassmembers class * { @androidx.compose.runtime.Composable <methods>; }
-keep class androidx.compose.runtime.reflect.ComposableMethod { *; }

# Coil
-if class coil.Coil
-keep class coil.Coil { *; }
-keep class coil.ImageLoaders { *; }
-keep class coil.request.ImageRequest$Builder { *; }
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Manter nomes de classes de dados (data classes) para evitar problemas com serialização ou reflexão
-keep class com.cebola.rekords.data.** { *; }
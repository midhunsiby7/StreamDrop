# StreamDrop ProGuard / R8 Rules

# Disable bytecode optimization & obfuscation to prevent R8 ConcurrentModificationException in AGP 8.5.2
-dontoptimize
-dontobfuscate

# 1. Native JNI methods across all libraries
-keepclasseswithmembernames class * {
    native <methods>;
}

# 2. youtube-dl / yt-dlp & FFmpeg libraries (both legacy com.yausername and io.github.junkfood02)
-keep class com.yausername.youtubedl_android.** { *; }
-keepclassmembers class com.yausername.youtubedl_android.** { *; }
-keep class com.yausername.ffmpeg.** { *; }
-keepclassmembers class com.yausername.ffmpeg.** { *; }

-keep class io.github.junkfood02.youtubedl_android.** { *; }
-keepclassmembers class io.github.junkfood02.youtubedl_android.** { *; }
-keep class io.github.junkfood02.ffmpeg.** { *; }
-keepclassmembers class io.github.junkfood02.ffmpeg.** { *; }

# 3. StreamDrop App Models & Data Classes
-keep class com.streamdrop.app.core.data.ytdlp.** { *; }
-keepclassmembers class com.streamdrop.app.core.data.ytdlp.** { *; }

# 4. Room Database & Entities
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keep class com.streamdrop.app.core.data.db.** { *; }

# 5. WorkManager & Hilt Workers
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class com.streamdrop.app.core.worker.** { *; }

# 6. Hilt Dependency Injection
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends androidx.lifecycle.ViewModel

# 7. Gson Serialization
-keepattributes Signature, InnerClasses, EnclosingMethod, *Annotation*
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# 8. Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class * extends kotlinx.coroutines.AbstractCoroutine {
    *** getCompletion(...);
}

# 9. Suppress warnings for optional / internal dependencies
-dontwarn org.slf4j.**
-dontwarn javax.annotation.**
-dontwarn io.github.junkfood02.**
-dontwarn com.yausername.**

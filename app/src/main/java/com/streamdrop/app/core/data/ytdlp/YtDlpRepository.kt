package com.streamdrop.app.core.data.ytdlp

import android.content.Context
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YtDlpRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    /**
     * Gets the path to the executable yt-dlp binary.
     * Since we bundled it as `libytdlp.so` in jniLibs and set extractNativeLibs="true",
     * Android extracts it to `applicationInfo.nativeLibraryDir`.
     */
    private fun getExecutablePath(): String {
        val nativeLibraryDir = context.applicationInfo.nativeLibraryDir
        val ytdlpFile = File(nativeLibraryDir, "libytdlp.so")
        
        if (!ytdlpFile.exists()) {
            throw IllegalStateException("yt-dlp binary not found at $nativeLibraryDir/libytdlp.so")
        }
        
        // Ensure it has execute permissions
        ytdlpFile.setExecutable(true, false)
        return ytdlpFile.absolutePath
    }

    /**
     * Analyzes a URL by running `yt-dlp -J <url>` to get the JSON dump.
     */
    suspend fun analyzeUrl(url: String): Result<YtDlpMetadata> = withContext(Dispatchers.IO) {
        try {
            val executable = getExecutablePath()
            
            // Build the command
            val processBuilder = ProcessBuilder(
                executable,
                "--dump-json",
                "--no-playlist", // Just the single video
                url
            )
            
            val process = processBuilder.start()
            
            // Read output
            val output = process.inputStream.bufferedReader().use { it.readText() }
            val error = process.errorStream.bufferedReader().use { it.readText() }
            
            val exitCode = process.waitFor()
            
            if (exitCode == 0 && output.isNotEmpty()) {
                val metadata = gson.fromJson(output, YtDlpMetadata::class.java)
                Result.success(metadata)
            } else {
                Result.failure(Exception("yt-dlp failed with exit code $exitCode: $error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

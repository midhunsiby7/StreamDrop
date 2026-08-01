package com.streamdrop.app.core.data.ytdlp

import com.google.gson.annotations.SerializedName

/**
 * Data classes mapping the JSON output of `yt-dlp --dump-json`.
 * Since yt-dlp outputs a lot of fields, we only parse what we need.
 */

data class YtDlpMetadata(
    val id: String?,
    val title: String?,
    val description: String?,
    val uploader: String?,
    val channel: String?,
    val duration: Long?, // in seconds
    @SerializedName("upload_date") val uploadDate: String?, // YYYYMMDD
    val thumbnail: String?,
    val formats: List<YtDlpFormat>?
)

data class YtDlpFormat(
    @SerializedName("format_id") val formatId: String?,
    @SerializedName("format_note") val formatNote: String?, // e.g., "1080p", "medium"
    val ext: String?, // "mp4", "webm", "m4a"
    val resolution: String?, // e.g., "1920x1080" or "audio only"
    val vcodec: String?, // "none" for audio-only
    val acodec: String?, // "none" for video-only
    val filesize: Long?, // sometimes null, try filesize_approx
    @SerializedName("filesize_approx") val filesizeApprox: Long?,
    val fps: Float?
) {
    val isAudioOnly: Boolean
        get() = vcodec == "none"

    val isVideoOnly: Boolean
        get() = acodec == "none"
        
    val hasAudioAndVideo: Boolean
        get() = vcodec != "none" && acodec != "none"
}

<div align="center">

# 🚀 StreamDrop

### A modern, fast and beautiful YouTube downloader for Android

Download YouTube videos and audio directly on your Android device with a clean Material 3 interface, background downloads, and high-performance processing powered by **yt-dlp** and **FFmpeg**.

<img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white"/>
<img src="https://img.shields.io/badge/Kotlin-2.x-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>
<img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge"/>
<img src="https://img.shields.io/badge/Architecture-MVVM-orange?style=for-the-badge"/>
<img src="https://img.shields.io/badge/License-MIT-blue?style=for-the-badge"/>

---

*A fast, reliable and fully on-device YouTube downloader built with modern Android technologies.*

</div>

---

# ✨ Features

### 🎥 Video Downloads

- Download YouTube videos in MP4
- Multiple quality selection
- Smart format selection
- Rename before downloading
- Background downloading

---

### 🎵 Audio Downloads

- Convert videos directly to MP3
- Optimized audio extraction
- Smaller output size
- Automatic duplicate filename handling

---

### 📱 Beautiful User Experience

- Material 3 Design
- Dynamic progress tracking
- Modern animations
- Download History
- Play downloaded files
- Share downloads
- Delete downloads
- Clean navigation

---

### ⚡ Performance

- Background downloads using WorkManager
- Coroutine powered architecture
- Optimized download pipeline
- Efficient Room database
- Cached metadata
- Optimized APK size
- Release build optimization

---

# 📸 Screenshots

> *(Add screenshots here)*

| Home | Analyze | Download | History |
|------|----------|-----------|-----------|
| 📷 | 📷 | 📷 | 📷 |

---

# 🏗 Architecture

```
                Presentation Layer
┌────────────────────────────────────────────┐
│             Jetpack Compose UI             │
│                ViewModels                  │
└────────────────────────────────────────────┘
                    │
                    ▼
              Repository Layer
                    │
                    ▼
┌────────────────────────────────────────────┐
│ Room Database │ yt-dlp │ FFmpeg │ Storage │
└────────────────────────────────────────────┘
```

The project follows **MVVM Architecture** with Kotlin Coroutines and Repository Pattern for a clean, scalable and maintainable codebase.

---

# 🛠 Built With

- Kotlin
- Jetpack Compose
- Material 3
- AndroidX
- MVVM
- Room Database
- WorkManager
- Kotlin Coroutines
- yt-dlp
- FFmpeg
- Coil
- Android Storage Framework

---

# 🚀 Download Flow

```
Paste URL
      │
      ▼
Analyze Video
      │
      ▼
Fetch Metadata
      │
      ▼
Select Format
      │
      ▼
Choose Quality
      │
      ▼
Download
      │
      ▼
History
```

---

# ⚙ Engineering Journey

This project was much more than simply building a downloader.

It involved weeks of debugging, optimization, profiling and testing to make the application stable on real Android devices.

Some of the engineering challenges solved include:

✅ WorkManager foreground service crashes

✅ Foreground notification issues

✅ yt-dlp integration

✅ FFmpeg post-processing

✅ MP3 extraction pipeline

✅ Duplicate filename handling

✅ Progress synchronization

✅ Background download management

✅ Download cancellation

✅ File validation

✅ Scoped Storage compatibility

✅ APK size optimization

✅ Release build optimization

✅ Performance tuning

Every major feature was repeatedly tested on physical devices to ensure reliability before release.

---

# 📂 Project Structure

```
app
│
├── data
├── database
├── repository
├── worker
├── ui
├── navigation
├── util
├── model
└── MainActivity.kt
```

---

# 📦 Installation

Download the latest APK from the **Releases** page.

Install.

Paste a YouTube URL.

Analyze.

Choose Video or Audio.

Download.

Enjoy.

---

# 🔥 Why StreamDrop?

Unlike many downloaders, StreamDrop performs everything **locally on your device**.

No external servers.

No cloud processing.

No third-party API.

Everything happens securely on-device using **yt-dlp** and **FFmpeg**.

---

# 📈 Future Roadmap

- Playlist Downloads
- Subtitle Downloads
- Batch Downloads
- Download Queue
- Scheduled Downloads
- SponsorBlock Integration
- Material You Dynamic Color
- Better Folder Navigation
- In-app Update Checker

---

# 🤝 Contributing

Contributions are always welcome.

Feel free to

- Fork the repository
- Create a new branch
- Commit your improvements
- Open a Pull Request

---

# 📄 License

This project is licensed under the MIT License.

---

# ❤️ Acknowledgements

Special thanks to these amazing open-source projects:

- yt-dlp
- FFmpeg
- AndroidX
- Jetpack Compose
- Kotlin
- Material Design

Without these projects, StreamDrop would not have been possible.

---

<div align="center">

## ⭐ If you like this project...

Please consider giving it a **Star ⭐**

It really helps and motivates future development.

---

Made with ❤️ using Kotlin & Jetpack Compose

</div>

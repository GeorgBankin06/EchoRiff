# 🎧 EchoRiff – Stream Your Favorite Radio Anytime, Anywhere

**EchoRiff** is a modern Android application designed for seamless radio streaming. Built with cutting-edge technologies like **ExoPlayer**, **Firebase**, and **Jetpack Compose**, EchoRiff brings a personalized, responsive, and immersive experience to radio lovers around the world.

---

## 📱 Features

- 🔊 **Live Radio Streaming** — Play radio stations from around the world in real-time using **ExoPlayer**.
- ❤️ **Favorite Stations & Songs** — Like stations and songs to save them for easy access.
- 📚 **Custom Playlists** — Create and manage your own playlists for both songs and radios.
- 🔴 **Recording Functionality** — Record live radio streams locally and play them back anytime.
- 📡 **Media Controls** — Full support for notification controls with **Media3** and **MediaSessionService**.
- 🔐 **User Authentication** — Secure login and registration using **Firebase Authentication**.
- ☁️ **Cloud Sync** — Store favorites and playlists in **Firebase Firestore**.
- 🌐 **Offline Playback** — Replay saved recordings even without internet access.
- 🎨 **Modern UI** — Built with **Material 3**, **MotionLayout**, and **Bottom Navigation**.

---

## 🚀 Tech Stack

| Area | Technology |
|------|------------|
| Language | Kotlin |
| Architecture | MVVM + Clean Architecture |
| Dependency Injection | Koin |
| Networking | OkHttp |
| Media Playback | ExoPlayer, Media3 |
| Authentication | Firebase Auth |
| Database | Firebase Firestore |
| Image Loading | Coil |
| UI Components | Material 3, MotionLayout |
| Testing | MockK, JUnit, Coroutine Test |
| Build Tool | Gradle (KTS) |

---

## 🛠️ Project Structure

```
com.echoriff.echoriff
│
├── auth              # Authentication flow (login/register)
├── radio             # Radio streaming logic and models
├── favorite          # Favorites and liked content
├── playlist          # User playlists
├── record            # Audio recording functionality
├── common            # Shared components (models, utils, base classes)
├── main              # Main UI and navigation
└── di                # Dependency injection (Koin modules)
```

---

## 🧪 Testing

- Uses `MockK` and `JUnit` for unit testing.
- Coroutine-based tests with `kotlinx-coroutines-test`.
- ViewModel and use case logic are thoroughly tested.

Example:
```kotlin
@Test
fun `fetchLikedSongs sets state to Success`() = runTest {
    coEvery { fetchLikedSongsUseCase.fetchLikedSongs() } returns listOf(mockSong)
    viewModel.fetchLikedSongs()
    assert(viewModel.uiState.value is Success)
}
```

---

## 📝 Installation & Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/EchoRiff.git
   ```

2. Open in **Android Studio Hedgehog or newer**.

3. Configure Firebase:
   - Add your `google-services.json` file to the `app/` directory.
   - Ensure Firebase Authentication and Firestore are enabled in the Firebase console.

4. Build and run on an Android 8.0+ device or emulator.

---

## 🔒 Permissions

EchoRiff requires the following permissions:
- `INTERNET` – for streaming audio.
- `FOREGROUND_SERVICE` – for background playback and recording.
- `RECORD_AUDIO` – for capturing audio (if using AudioPlaybackCapture).
- `WRITE_EXTERNAL_STORAGE` – to save audio recordings locally.

---

## 🧠 Future Enhancements

- AI-powered song suggestions.
- Cross-platform version (iOS).
- More personalization options (themes, layouts).
- Enhanced offline mode.
- Scheduled recordings.

---

## 🤝 Contributing

Contributions are welcome!  
Please open issues or pull requests if you'd like to help improve EchoRiff.

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

## 🙌 Acknowledgements

- [ExoPlayer](https://github.com/google/ExoPlayer)
- [Firebase](https://firebase.google.com/)
- [Material Design 3](https://m3.material.io/)
- [Jetpack Libraries](https://developer.android.com/jetpack)
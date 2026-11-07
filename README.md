# NK PLAYER

Native Android media player built with Kotlin and Jetpack Compose.

## Current status

Stage 1 — native project foundation and first dashboard UI.

- Kotlin + Jetpack Compose
- Material 3 dark interface
- Home, Audio, Video and Library navigation
- Android media permissions declared
- Media3 dependencies included for the playback stage
- GitHub Actions debug APK workflow

## Build

Requires JDK 17 and Gradle 8.13.

```bash
gradle :app:assembleDebug
```

The debug APK is generated at `app/build/outputs/apk/debug/app-debug.apk`.

## Roadmap

1. Device media scanning and permission handling
2. Audio playback service and notification controls
3. Video playback and fullscreen controls
4. Favorites, playlists, history and resume position
5. Equalizer, playback speed and sleep timer
6. UI polish, testing and release build

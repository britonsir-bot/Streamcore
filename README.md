# StreamCore Android — starter v0.1

A runnable Android Studio starter for a universal streaming shell. This initial version includes:
- Native Kotlin + Jetpack Compose UI
- Platform selector (YouTube / TikTok / Twitch)
- Adaptive demo feed layout (vertical-video layout for TikTok; cards for YouTube/Twitch)
- Local filtering preferences: suspected AI-content filter, Shorts toggle, keyword exclusion
- Platform profile settings persisted locally using Preferences DataStore
- Session Vault placeholder that deliberately does **not** import raw cookies or ask for passwords

## Build
Open this folder in Android Studio (Giraffe or newer), sync Gradle, and run on Android 8+ (API 26).

This is an app shell and demonstrator. Platform adapters, authenticated stream extraction, ad filtering and live platform integration are not implemented in this starter. Integrations must use legitimate, supported access paths and be independently maintained.

## Package
`com.streamcore.app`

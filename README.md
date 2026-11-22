# CatWithUmbrella

CatWithUmbrella is an Android app that uses your location and the OpenWeather forecast to answer the question: “Do I need an umbrella?” It cycles through the forecast for today, tomorrow, and the weekend.

## Requirements

- Android Studio version compatible with Android Gradle Plugin 9.2.1
- JDK 21
- Android SDK Platform 37
- A device or emulator with Google Play services and location enabled

## Open and build

Open the repository root in Android Studio and sync the Gradle project. To build a debug APK from the command line:

```shell
./gradlew :app:assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

The app requires location permission to get a forecast for the device's current location. On an emulator, set a simulated location in the emulator's **Extended controls > Location** panel. Notification permission is requested on Android 13 and later for scheduled rain alerts.

## How it works

- The UI is built with Jetpack Compose and Material 3.
- The app gets a current location through Google Play services, then requests the OpenWeather five-day forecast.
- The screen cycles from today to tomorrow after 3 seconds, then to the weekend after another 4 seconds. Tap the screen to restart the cycle.
- WorkManager checks for tomorrow's rain every 6 hours when an unmetered network is available. If rain is forecast, the worker posts a notification.
- Debug builds log location and forecast progress with Timber and log HTTP request summaries with OkHttp.

## Configuration

The OpenWeather API key is currently stored in `app/src/main/java/com/tacocat/catwithumbrella/Constants.kt`. Replace it with your own key if needed. Avoid committing private API keys to a public repository.

Dependency versions are maintained in `gradle/libs.versions.toml`.

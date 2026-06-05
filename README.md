# Vestra (formerly ScreenStamp)

Vestra is a specialized Android application designed to display a static graphic overlay (image) on top of all active windows and installed applications. 

## Features
- **Custom Overlay Image**: Load any image from your device's storage. The app creates a local copy to ensure the image remains available even if the original is deleted.
- **Precise Positioning**: Configure the exact Width, Height, and X/Y coordinates in pixels.
- **Click-Through Capabilities**: The displayed overlay intercepts absolutely no touches. All taps, swipes, and long-presses pass right through the image to the app underneath.
- **100% Opacity**: The overlay is completely opaque and visually authentic without borders, shadows, or Android system dimming.
- **External Triggers**: Vestra can be controlled via external system intents (Broadcasts), making it easy to integrate with automation tools like Tasker or ADB macros.

## How It Works
The application uses an Android `Foreground Service` to keep the overlay active and stable in the background. It leverages the `WindowManager` API with the `TYPE_APPLICATION_OVERLAY` layout flag. 

To ensure clicks pass through, the service sets the `FLAG_NOT_FOCUSABLE` and `FLAG_HARDWARE_ACCELERATED` flags.

### API / External Commands
You can show or hide the overlay programmatically without opening the app UI:

**To Show the overlay:**
```bash
adb shell am broadcast -a com.screenstamp.ACTION_SHOW
```

**To Hide the overlay:**
```bash
adb shell am broadcast -a com.screenstamp.ACTION_HIDE
```

## Requirements
- Android 8.0 (API 26) or higher.
- Requires the "Draw over other apps" (System Alert Window) permission, which is prompted on the first launch.

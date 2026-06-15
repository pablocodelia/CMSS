# Codelia Music Study Suite (CMSS) 🎵

Codelia Music Study Suite (CMSS) is a high-performance Android utility application designed for musicians. Built using modern Android development practices, it features a **precise, low-latency metronome** and a **real-time chromatic tuner**.

---

## 🚀 Features

### ⏱️ Metronome
*   **Precision Audio Engine**: Streams a mathematically synthesized 1kHz sine-wave click using Android's native `AudioTrack` in streaming mode.
*   **Pop-Free Playback**: Features a linear amplitude envelope generator (fade-out) on each audio click to prevent hardware speaker popping.
*   **Tap Tempo**: Tap-to-detect functionality that calculates BPM using a rolling 4-tap average. It automatically clears history when idle for more than 2 seconds.
*   **BPM Range**: Flexible tempos ranging from 40 to 300 BPM, controlled via step buttons (`+` / `-`) or a swift slider.
*   **Visual Beat Indicators**: Synchronized Compose-native indicators showcasing the current beat in a 4/4 cycle.

### 🎸 Chromatic Tuner
*   **YIN Pitch Estimation**: Integrates the high-precision YIN pitch detection algorithm (via `TarsosDSP`) for rapid and accurate real-time frequency analysis.
*   **Low-Level Audio Capture**: Configures Android's `AudioRecord` API to pipe audio streams directly into the DSP engine at a sample rate of 22050Hz.
*   **Interactive Cents Gauge**: Uses Jetpack Compose Canvas to draw a dynamic needle meter showing sharp/flat deviation within a $\pm$50 cents range.
*   **In-Tune feedback**: The note indicator turns green and centers the needle when the note is in-tune (within $\pm$5 cents).
*   **Permissions Handling**: Seamlessly requests runtime `RECORD_AUDIO` microphone permission.

---

## 🛠️ Technology Stack & Architecture

*   **Language**: Kotlin 1.9+
*   **UI Framework**: Jetpack Compose (100% declarative UI)
*   **Architecture Pattern**: Clean MVVM (Model-View-ViewModel)
*   **Navigation**: Jetpack Compose Navigation
*   **Concurreny & Async**: Kotlin Coroutines & StateFlow for state management
*   **Audio Engine**: Android `AudioTrack`, `AudioRecord`, and custom wrapped `TarsosDSP` streams.

---

## 📂 Project Structure

```text
app/src/main/kotlin/com/musicstudiosuite/
│
├── MainActivity.kt            # Entry point of the application
├── MainScreen.kt              # App Scaffold & Bottom Bar Navigation
│
├── theme/                     # Compose UI Color, Typography, & Theme setup
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
│
├── metronome/                 # Metronome Module
│   ├── MetronomeEngine.kt     # Low-level AudioTrack synthesizer
│   ├── MetronomeViewModel.kt  # State, BPM controls, & Tap Tempo algorithm
│   └── MetronomeScreen.kt     # UI elements & controls
│
└── tuner/                     # Tuner Module
    ├── AndroidAudioGlue.kt    # Bridge between AudioRecord and TarsosDSP
    ├── TunerEngine.kt         # YIN Pitch estimation processor
    ├── TunerViewModel.kt      # Frequency-to-note mathematical mapping
    └── TunerScreen.kt         # Cents gauge Canvas UI & permissions launcher
```

---

## ⚙️ How to Build & Run

### Prerequisites
*   Android Studio (Ladybug or newer recommended)
*   Android SDK 34 (API Level 34 / Android 14)
*   Gradle 8.0+

### Setup
1.  Clone the repository:
    ```bash
    git clone https://github.com/pablocodelia/CMSS.git
    ```
2.  Open the project in Android Studio.
3.  Allow Gradle synchronization to download dependencies (including the `TarsosDSP` library).
4.  Run the application on a physical device or emulator with microphone support.

---

## 📋 Roadmap & Upcoming Features
*   [ ] Add time signature selection (e.g., 3/4, 6/8) and accent beat sounds.
*   [ ] Integrate chord charts and guitar/ukulele fretboard visualizers.
*   [ ] Add scale charts for practicing improvisation.
*   [ ] Custom tuning presets (Drop D, Half-Step Down, Open G, etc.).

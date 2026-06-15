# Codelia Music Study Suite (CMSS) 🎵

Codelia Music Study Suite (CMSS) es una aplicación de herramientas de alto rendimiento para Android diseñada para músicos. Construida utilizando prácticas modernas de desarrollo de Android, incluye un **metrónomo preciso de baja latencia** y un **afinador cromático en tiempo real**.

---

## 🚀 Características

### ⏱️ Metrónomo
*   **Motor de Audio de Precisión**: Transmite un clic de onda sinusoidal de 1kHz sintetizado matemáticamente utilizando el `AudioTrack` nativo de Android en modo de streaming.
*   **Reproducción Libre de Chasquidos**: Cuenta con un generador de envolvente de amplitud lineal (fade-out) en cada clic de audio para evitar chasquidos en los altavoces del hardware.
*   **Tap Tempo**: Funcionalidad de tocar para detectar que calcula los BPM utilizando un promedio móvil de 4 toques. Limpia automáticamente el historial si permanece inactivo durante más de 2 segundos.
*   **Rango de BPM**: Tiempos flexibles que van desde 40 hasta 300 BPM, controlados mediante botones de paso (`+` / `-`) o un deslizador rápido.
*   **Indicadores Visuales de Pulso**: Indicadores nativos de Compose sincronizados que muestran el pulso actual en un ciclo de 4/4.

### 🎸 Afinador Cromático
*   **Estimación de Tono YIN**: Integra el algoritmo de detección de tono YIN de alta precisión (a través de `TarsosDSP`) para un análisis de frecuencia rápido y preciso en tiempo real.
*   **Captura de Audio de Bajo Nivel**: Configura la API `AudioRecord` de Android para dirigir los flujos de audio directamente al motor DSP a una frecuencia de muestreo de 22050Hz.
*   **Indicador de Centésimas Interactivo**: Utiliza Jetpack Compose Canvas para dibujar un indicador de aguja dinámico que muestra la desviación de bemol/sostenido dentro de un rango de $\pm$50 centésimas (cents).
*   **Retroalimentación de Afinación**: El indicador de nota se vuelve verde y centra la aguja cuando la nota está afinada (dentro de $\pm$5 centésimas).
*   **Gestión de Permisos**: Solicita de manera fluida el permiso de micrófono `RECORD_AUDIO` en tiempo de ejecución.

---

## 🛠️ Tecnologías y Arquitectura

*   **Lenguaje**: Kotlin 1.9+
*   **Framework de UI**: Jetpack Compose (UI 100% declarativa)
*   **Patrón de Arquitectura**: MVVM Limpio (Model-View-ViewModel)
*   **Navegación**: Jetpack Compose Navigation
*   **Concurrencia y Asincronía**: Kotlin Coroutines y StateFlow para la gestión del estado
*   **Motor de Audio**: Android `AudioTrack`, `AudioRecord`, y flujos personalizados de `TarsosDSP`.

---

## 📂 Estructura del Proyecto

```text
app/src/main/kotlin/com/musicstudiosuite/
│
├── MainActivity.kt            # Punto de entrada de la aplicación
├── MainScreen.kt              # Scaffold de la aplicación y navegación de la barra inferior
│
├── theme/                     # Configuración de colores, tipografía y temas de Compose
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
│
├── metronome/                 # Módulo del metrónomo
│   ├── MetronomeEngine.kt     # Sintetizador de AudioTrack de bajo nivel
│   ├── MetronomeViewModel.kt  # Estado, controles de BPM y algoritmo de Tap Tempo
│   └── MetronomeScreen.kt     # Elementos de UI y controles
│
└── tuner/                     # Módulo del afinador
    ├── AndroidAudioGlue.kt    # Puente entre AudioRecord y TarsosDSP
    ├── TunerEngine.kt         # Procesador de estimación de tono YIN
    ├── TunerViewModel.kt      # Mapeo matemático de frecuencia a nota musical
    └── TunerScreen.kt         # Interfaz de usuario de Canvas para el indicador y lanzador de permisos
```

---

## ⚙️ Cómo Compilar y Ejecutar

### Prerrequisitos
*   Android Studio (se recomienda Ladybug o más reciente)
*   Android SDK 34 (API Level 34 / Android 14)
*   Gradle 8.0+

### Configuración
1.  Clonar el repositorio:
    ```bash
    git clone https://github.com/pablocodelia/CMSS.git
    ```
2.  Abrir el proyecto en Android Studio.
3.  Permitir la sincronización de Gradle para descargar las dependencias (incluyendo la biblioteca `TarsosDSP`).
4.  Ejecutar la aplicación en un dispositivo físico o emulador con soporte para micrófono.

---

## 📋 Hoja de Ruta y Próximas Funciones
*   [ ] Agregar selección de compás (por ejemplo, 3/4, 6/8) y sonidos de pulso acentuado.
*   [ ] Integrar diagramas de acordes y visualizadores de diapasón para guitarra/ukelele.
*   [ ] Agregar tablas de escalas para practicar la improvisación.
*   [ ] Ajustes preestablecidos de afinación personalizados (Drop D, medio tono abajo, Open G, etc.).

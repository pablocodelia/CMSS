# Music Studio Suite - Seguimiento del Proyecto

## 📋 Resumen de Estado
- **Objetivo:** Music Studio Suite para Android (Metrónomo y Afinador).
- **Estado Actual:** ¡Éxito! Aplicación optimizada y funcionando correctamente sin bloqueos ni deriva temporal.
- **Nombre de Paquete:** `com.musicstudiosuite`
- **Compatibilidad:** AGP 8.7.3 / SDK 35.

---

## ✅ Tareas Completadas
- [x] Configuración Inicial del Proyecto (Jetpack Compose).
- [x] Implementación del Motor del Metrónomo (`AudioTrack`).
- [x] Implementación del Motor del Afinador (`TarsosDSP` con acople personalizado).
- [x] Navegación e Interfaz de Usuario (Material 3).
- [x] Degradación de AGP/Gradle para compatibilidad con Android Studio.
- [x] Refactorización del nombre de paquete de `com.example...` a `com.musicstudiosuite`.
- [x] Desinstalación manual e instalación limpia en el dispositivo.
- [x] **Optimización de Rendimiento:** Metrónomo sample-accurate (0ms drift) e isolación de recomposiciones Compose.

---

## 🔍 Cola de Depuración (Completada)
- [x] **Capturar Stack Trace:** Completado.
- [x] **Prueba de Lanzador Mínimo:** ¡Éxito! La aplicación abre con `MinimalActivity`.
- [x] **Simplificación de MainActivity:** Reintegrado con MainActivity completo y funcionando.
- [x] **Reintegración de Componentes:** Todos los componentes se cargan correctamente sin fallas de inicialización.
- [x] **Verificación de Hardware de Audio:** AudioTrack y AudioRecord verificados y funcionando de forma segura.

---

## 🛠️ Plan de Acción UNIACC (Unidad 3)
Lista de tareas específicas organizadas por módulos y capas arquitectónicas para implementar de manera rigurosa las recomendaciones teóricas de la Unidad 3 de la UNIACC:

### ⚙️ Capa de Configuración Global y Construcción
- [ ] **Configurar Ofuscación y Optimización de Código (R8)**
  * Abrir el archivo `app/build.gradle.kts`.
  * Asegurar que en el bloque de `buildTypes.release` estén activadas:
    ```kotlin
    isMinifyEnabled = true
    isShrinkResources = true
    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    ```
  * *Justificación:* Protección frente a ingeniería inversa y ofuscación de clases críticas de procesamiento de señal.

### 🔒 Capa de Datos y Seguridad (data / security)
- [ ] **Crear el módulo o clase de persistencia segura (`SecurePreferencesManager`)**
  * Crear la clase `SecurePreferencesManager.kt` para centralizar el guardado de configuración rítmica o Hz base del afinador.
  * Implementar el uso de `EncryptedSharedPreferences` delegando la creación de la clave maestra a `MasterKey`.
  * Reemplazar inicializaciones de `SharedPreferences` estándar.
- [ ] **Añadir interceptor de red seguro**
  * Configurar llamadas de red (ej. con OkHttp/Retrofit) para forzar estrictamente HTTPS.
  * Configurar validación de certificados (Certificate Pinning) para mitigar ataques MITM.

### 🔑 Capa de Presentación y Autenticación (ui / auth)
- [ ] **Implementar el flujo de autenticación biométrica**
  * Crear un manager `BiometricAuthHandler.kt` que invoque la API oficial `BiometricPrompt` de Android.
  * Configurar niveles de autenticación permitidos usando `BIOMETRIC_STRONG`.
  * Implementar mecanismo de respaldo (PIN o patrón) mediante `DEVICE_CREDENTIAL` en caso de fallo o cancelación.
  * Asegurar que la app solo valide la confirmación del token emitido por el SO, impidiendo almacenamiento local de huellas o rostros.

### ♿ Capa de Interfaz de Usuario y Accesibilidad (ui / components)
- [ ] **Auditar e inyectar semántica para Lectores de Pantalla (TalkBack)**
  * Revisar [MetronomeScreen.kt](file:///D:/Pablo/Software/Development/Android/MusicStudioSuite/app/src/main/kotlin/com/musicstudiosuite/metronome/MetronomeScreen.kt) y [TunerScreen.kt](file:///D:/Pablo/Software/Development/Android/MusicStudioSuite/app/src/main/kotlin/com/musicstudiosuite/tuner/TunerScreen.kt).
  * Añadir el parámetro obligatorio `contentDescription` con strings explicativos en todos los elementos interactivos `IconButton`, `Slider` y en el Canvas gráfico.
- [ ] **Garantizar la Escalabilidad del Texto (Responsive Text)**
  * Auditar todos los archivos dentro del paquete `theme/` y componentes de pantalla.
  * Eliminar declaraciones de tamaño de texto forzada en unidades de pixeles absolutos o fijos.
  * Reemplazar por unidades de escala dinámicas `sp` para respetar la configuración nativa del sistema operativo.
- [ ] **Expandir Dimensiones de Áreas Táctiles (Accesibilidad Motora)**
  * Modificar los modificadores (`Modifier`) de los botones de reproducción, incremento/decremento (`+` / `-`) y de `Tap Tempo`.
  * Usar `Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)` para cumplir con el estándar internacional.
  * Añadir padding para mitigar toques accidentales entre elementos adyacentes.

### 📊 Capa de Métricas y Experimentación (analytics / ux)
- [ ] **Diseñar la infraestructura modular de Analíticas y Pruebas A/B**
  * Crear la interfaz `AnalyticsProvider.kt` bajo una arquitectura desacoplada.
  * Crear dos variantes del botón de captura de ritmo (Tap Tempo) en la interfaz:
    * *Variante A:* Diseño integrado cromáticamente con colores neutros.
    * *Variante B:* Diseño de alto contraste (mínimo de ratio 4.5:1) y dimensiones extendidas a $64\times64\text{ dp}$.
  * Configurar una bandera de asignación aleatoria persistente para dividir el flujo de usuarios y medir la tasa de uso.
  * *Principio ético:* Añadir validación de consentimiento que garantice la anonimización total de las interacciones antes de procesarse.

---

## 📝 Notas del Usuario
- Dispositivo: Xiaomi / MIUI.
- Opción "Instalar a través de USB" activada en las Opciones de Desarrollador.

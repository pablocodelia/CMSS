# Conservar puntos de entrada nativos de Android y Jetpack Compose
-keepclasseswithmembers class * {
    public static void main(java.lang.String[]);
}

# Conservar metadatos y serialización
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Reglas específicas para conservar la integridad del motor DSP (TarsosDSP)
-keep class be.tarsos.dsp.** { *; }
-dontwarn be.tarsos.dsp.**

# Evitar la ofuscación de los motores de audio locales para depuración precisa de stacktraces
-keep class com.musicstudiosuite.metronome.MetronomeEngine {
    public void start(***);
    public void stop();
    public void setBpm(int);
}

-keep class com.musicstudiosuite.tuner.TunerEngine {
    public void startListening();
    public void stopListening();
}

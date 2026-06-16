package com.musicstudiosuite.tuner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TunerScreen(viewModel: TunerViewModel = viewModel()) {
    val isListening by viewModel.isListening.collectAsState()
    val context = LocalContext.current

    // Automatically stop listening when navigated away (disposed) to prevent battery drain
    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopListening()
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleListening()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Tuner",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Cents Gauge (Isolated container to prevent full screen recompositions)
        TunerGauge(tuningResultFlow = viewModel.tuningResult)

        Spacer(modifier = Modifier.height(48.dp))

        // Note Display (Isolated container)
        NoteDisplay(tuningResultFlow = viewModel.tuningResult)

        // Frequency Display (Isolated container)
        FrequencyDisplay(tuningResultFlow = viewModel.tuningResult)

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = {
                val permissionCheckResult = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
                if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                    viewModel.toggleListening()
                } else {
                    launcher.launch(Manifest.permission.RECORD_AUDIO)
                }
            },
            modifier = Modifier.sizeIn(minWidth = 160.dp, minHeight = 48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isListening) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        ) {
            Text(if (isListening) "Stop Tuning" else "Start Tuning")
        }
    }
}

@Composable
fun TunerGauge(tuningResultFlow: kotlinx.coroutines.flow.StateFlow<TuningResult>) {
    val result by tuningResultFlow.collectAsState()
    
    val centsInt = result.cents.toInt()
    val centsAbs = if (centsInt < 0) -centsInt else centsInt
    val accessibilityDescription = when {
        !result.isDetected -> "Afinador inactivo, esperando sonido"
        result.cents in -5f..5f -> "Afinado. Nota ${result.note} exacta"
        result.cents < -5f -> "Nota ${result.note} calada por $centsAbs centésimas, bemol"
        else -> "Nota ${result.note} alta por $centsAbs centésimas, sostenido"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .semantics {
                contentDescription = accessibilityDescription
            },
        contentAlignment = Alignment.Center
    ) {
        CentsGauge(cents = result.cents, isDetected = result.isDetected)
    }
}

@Composable
fun NoteDisplay(tuningResultFlow: kotlinx.coroutines.flow.StateFlow<TuningResult>) {
    val result by tuningResultFlow.collectAsState()
    
    val notaLimpia = result.note.replace("[0-9]".toRegex(), "")
    val octava = result.note.replace("[^0-9]".toRegex(), "")
    val nombreNota = when (notaLimpia) {
        "C" -> "Do"
        "C#" -> "Do sostenido"
        "D" -> "Re"
        "D#" -> "Re sostenido"
        "E" -> "Mi"
        "F" -> "Fa"
        "F#" -> "Fa sostenido"
        "G" -> "Sol"
        "G#" -> "Sol sostenido"
        "A" -> "La"
        "A#" -> "La sostenido"
        "B" -> "Si"
        else -> notaLimpia
    }
    
    val accessibilityNote = if (result.isDetected) {
        if (octava.isNotEmpty()) "Nota detectada: $nombreNota, octava $octava" else "Nota detectada: $nombreNota"
    } else {
        "Ninguna nota detectada"
    }

    Text(
        text = if (result.isDetected) result.note else "-",
        fontSize = 100.sp,
        fontWeight = FontWeight.Bold,
        color = if (result.isDetected && result.cents in -5f..5f) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.semantics {
            contentDescription = accessibilityNote
        }
    )
}

@Composable
fun FrequencyDisplay(tuningResultFlow: kotlinx.coroutines.flow.StateFlow<TuningResult>) {
    val result by tuningResultFlow.collectAsState()
    Text(
        text = if (result.isDetected) String.format("%.2f Hz", result.frequency) else "No sound detected",
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun CentsGauge(cents: Float, isDetected: Boolean) {
    // We removed animateFloatAsState to eliminate 60fps animation overhead.
    // The raw pitch detection updates at 21 times/sec, providing smooth, real-time feedback with zero visual lag.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        val onSurfaceColor = MaterialTheme.colorScheme.onSurfaceVariant

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerY = height / 2
            val centerX = width / 2

            // Draw scale
            drawLine(
                color = onSurfaceColor,
                start = Offset(0f, centerY),
                end = Offset(width, centerY),
                strokeWidth = 2.dp.toPx()
            )

            // Draw markers
            for (i in -50..50 step 10) {
                val x = centerX + (i / 50f) * (width / 2)
                val tickHeight = if (i % 50 == 0) 40.dp.toPx() else 20.dp.toPx()
                drawLine(
                    color = onSurfaceColor,
                    start = Offset(x, centerY - tickHeight / 2),
                    end = Offset(x, centerY + tickHeight / 2),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // Draw needle
            if (isDetected) {
                val needleX = centerX + (cents / 50f) * (width / 2)
                val needleColor = if (cents in -5f..5f) Color(0xFF4CAF50) else Color.Red
                drawLine(
                    color = needleColor,
                    start = Offset(needleX, centerY - 50.dp.toPx()),
                    end = Offset(needleX, centerY + 50.dp.toPx()),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

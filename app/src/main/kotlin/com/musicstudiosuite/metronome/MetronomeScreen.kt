package com.musicstudiosuite.metronome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MetronomeScreen(viewModel: MetronomeViewModel = viewModel()) {
    val bpm by viewModel.bpm.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Metronome",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Beat Indicators (Isolated to prevent full-screen recomposition on every beat)
        BeatIndicators(currentBeatFlow = viewModel.currentBeat, isPlayingFlow = viewModel.isPlaying)

        Spacer(modifier = Modifier.height(48.dp))

        // BPM Display
        Text(
            text = bpm.toString(),
            fontSize = 80.sp,
            fontWeight = FontWeight.Bold
        )
        Text(text = "BPM", style = MaterialTheme.typography.bodyLarge)

        Spacer(modifier = Modifier.height(32.dp))

        // BPM Controls
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = { viewModel.decrementBpm() }) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease BPM")
            }
            
            Slider(
                value = bpm.toFloat(),
                onValueChange = { viewModel.setBpm(it.toInt()) },
                valueRange = 40f..300f,
                modifier = Modifier
                    .width(200.dp)
                    .semantics {
                        contentDescription = "Deslizador de velocidad del metrónomo"
                        stateDescription = "$bpm pulsos por minuto"
                    }
            )

            IconButton(onClick = { viewModel.incrementBpm() }) {
                Icon(Icons.Default.Add, contentDescription = "Increase BPM")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tap Tempo Button
        OutlinedButton(
            onClick = { viewModel.tapTempo() },
            modifier = Modifier.width(120.dp),
            shape = CircleShape
        ) {
            Text("TAP", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Play/Stop Button
        Button(
            onClick = { viewModel.togglePlayback() },
            modifier = Modifier
                .size(80.dp)
                .clearAndSetSemantics {
                    contentDescription = if (isPlaying) "Detener metrónomo" else "Iniciar metrónomo"
                },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPlaying) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        ) {
            if (isPlaying) {
                // Stop icon (simple box)
                Box(modifier = Modifier.size(24.dp).background(Color.White))
            } else {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(32.dp))
            }
        }
    }
}

@Composable
fun BeatIndicators(
    currentBeatFlow: kotlinx.coroutines.flow.StateFlow<Int>,
    isPlayingFlow: kotlinx.coroutines.flow.StateFlow<Boolean>
) {
    val currentBeat by currentBeatFlow.collectAsState()
    val isPlaying by isPlayingFlow.collectAsState()

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                contentDescription = if (isPlaying) "Compás actual: ${currentBeat + 1} de 4" else "Indicador de compás inactivo"
                liveRegion = LiveRegionMode.Polite
            }
    ) {
        repeat(4) { i ->
            val isActive = i == currentBeat && isPlaying
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant)
            )
        }
    }
}

package com.example.ensayo.ui.directo

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private val LiveChordColor = Color(0xFFFFC107)
private const val DP_PER_SECOND_PER_LEVEL = 12

@Composable
fun DirectoScreen(
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DirectoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val speed by viewModel.speed.collectAsStateWithLifecycle()
    LiveModeEffect()
    DirectoContent(
        uiState = uiState,
        isPlaying = isPlaying,
        speed = speed,
        onTogglePlay = viewModel::togglePlay,
        onFaster = viewModel::faster,
        onSlower = viewModel::slower,
        onReachedEnd = viewModel::pause,
        onExit = onExit,
        modifier = modifier
    )
}

@Composable
fun DirectoContent(
    uiState: DirectoUiState,
    isPlaying: Boolean,
    speed: Int,
    onTogglePlay: () -> Unit,
    onFaster: () -> Unit,
    onSlower: () -> Unit,
    onReachedEnd: () -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        when (uiState) {
            DirectoUiState.Loading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
            DirectoUiState.NotFound -> Text(
                text = "Canción no encontrada",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
            is DirectoUiState.Success -> LiveSheet(
                state = uiState,
                isPlaying = isPlaying,
                speed = speed,
                onTogglePlay = onTogglePlay,
                onReachedEnd = onReachedEnd
            )
        }
        TextButton(
            onClick = onExit,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Text("Salir", color = Color.White)
        }
        if (uiState is DirectoUiState.Success) {
            SpeedControls(
                isPlaying = isPlaying,
                speed = speed,
                onTogglePlay = onTogglePlay,
                onFaster = onFaster,
                onSlower = onSlower,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun LiveSheet(
    state: DirectoUiState.Success,
    isPlaying: Boolean,
    speed: Int,
    onTogglePlay: () -> Unit,
    onReachedEnd: () -> Unit
) {
    val listState = rememberLazyListState()
    val pxPerSecond = with(LocalDensity.current) {
        (speed * DP_PER_SECOND_PER_LEVEL).dp.toPx()
    }

    // Autoscroll: en cada fotograma desplaza según el tiempo transcurrido
    LaunchedEffect(isPlaying, pxPerSecond) {
        if (!isPlaying) return@LaunchedEffect
        var lastFrame = withFrameNanos { it }
        while (true) {
            val frame = withFrameNanos { it }
            val seconds = (frame - lastFrame) / 1_000_000_000f
            lastFrame = frame
            listState.scrollBy(pxPerSecond * seconds)
            if (!listState.canScrollForward) {
                onReachedEnd()
                break
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .clickable(interactionSource = null, indication = null, onClick = onTogglePlay),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 48.dp, bottom = 120.dp)
    ) {
        item {
            Column {
                Text(
                    text = state.title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Tono: ${state.displayKey}",
                    color = LiveChordColor,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(24.dp))
            }
        }
        items(state.lines) { line ->
            Column {
                if (line.chords.isNotEmpty()) {
                    Text(
                        text = line.chords,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = state.fontSize.sp,
                        color = LiveChordColor
                    )
                }
                if (line.lyrics.isNotBlank()) {
                    Text(
                        text = line.lyrics,
                        fontFamily = FontFamily.Monospace,
                        fontSize = state.fontSize.sp,
                        color = Color.White
                    )
                } else if (line.chords.isEmpty()) {
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun SpeedControls(
    isPlaying: Boolean,
    speed: Int,
    onTogglePlay: () -> Unit,
    onFaster: () -> Unit,
    onSlower: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Color.DarkGray.copy(alpha = 0.85f), RoundedCornerShape(24.dp))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onSlower) { Text("−", color = Color.White, fontSize = 22.sp) }
        Text("Vel. $speed", color = Color.White)
        TextButton(onClick = onFaster) { Text("+", color = Color.White, fontSize = 22.sp) }
        TextButton(onClick = onTogglePlay) {
            Text(
                text = if (isPlaying) "Pausa" else "▶ Play",
                color = LiveChordColor,
                fontSize = 18.sp
            )
        }
    }
}

// Pantalla completa + pantalla siempre encendida mientras esta pantalla esté visible
@Composable
private fun LiveModeEffect() {
    val view = LocalView.current
    DisposableEffect(view) {
        val window = view.context.findActivity()?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        controller?.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller?.hide(WindowInsetsCompat.Type.systemBars())
        view.keepScreenOn = true
        onDispose {
            controller?.show(WindowInsetsCompat.Type.systemBars())
            view.keepScreenOn = false
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

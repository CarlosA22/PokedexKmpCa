package com.treino.pokedexkmpca.util

import androidx.compose.runtime.Composable

data class CaptureResult(
    val latitude: Double?,
    val longitude: Double?,
    val photoBytes: ByteArray?
)

interface HardwareManager {
    fun capture()
}

@Composable
expect fun rememberHardwareManager(onResult: (CaptureResult) -> Unit): HardwareManager

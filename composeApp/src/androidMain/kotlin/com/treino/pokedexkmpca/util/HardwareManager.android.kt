package com.treino.pokedexkmpca.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import java.io.ByteArrayOutputStream

class AndroidHardwareManager(
    private val context: Context,
    private val onResult: (CaptureResult) -> Unit,
    private val launchCamera: () -> Unit
) : HardwareManager {
    override fun capture() {
        launchCamera()
    }

    @SuppressLint("MissingPermission")
    fun handleCameraResult(bitmap: Bitmap?) {
        val bytes = bitmap?.let {
            val stream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            stream.toByteArray()
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = locationManager.getProviders(true)
        var bestLocation: Location? = null
        for (provider in providers) {
            val l = locationManager.getLastKnownLocation(provider) ?: continue
            if (bestLocation == null || l.accuracy < bestLocation.accuracy) {
                bestLocation = l
            }
        }

        onResult(
            CaptureResult(
                latitude = bestLocation?.latitude,
                longitude = bestLocation?.longitude,
                photoBytes = bytes
            )
        )
    }
}

@Composable
actual fun rememberHardwareManager(onResult: (CaptureResult) -> Unit): HardwareManager {
    val context = LocalContext.current
    var manager by remember { mutableStateOf<AndroidHardwareManager?>(null) }
    
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        manager?.handleCameraResult(bitmap)
    }

    return remember(onResult) {
        AndroidHardwareManager(context, onResult) {
            cameraLauncher.launch()
        }.also { manager = it }
    }
}

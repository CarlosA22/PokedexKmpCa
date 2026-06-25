package com.treino.pokedexkmpca.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.UIKit.*
import platform.CoreLocation.*
import platform.Foundation.*
import kotlinx.cinterop.*
import platform.darwin.NSObject
import platform.posix.memcpy

class IosHardwareManager(
    private val onResult: (CaptureResult) -> Unit
) : NSObject(), HardwareManager, UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol, CLLocationManagerDelegateProtocol {
    
    private val locationManager = CLLocationManager()
    private var lastLocation: CLLocation? = null

    init {
        locationManager.delegate = this
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()
    }

    override fun capture() {
        val picker = UIImagePickerController()
        if (UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
            picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
        } else {
            picker.sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        }
        picker.delegate = this
        
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(picker, true, null)
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun imagePickerController(picker: UIImagePickerController, didFinishPickingMediaWithInfo: Map<Any?, *>) {
        val image = didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
        val bytes = image?.let { uiImage ->
            val data = UIImageJPEGRepresentation(uiImage, 0.8)
            data?.let { nsData ->
                val result = ByteArray(nsData.length.toInt())
                result.usePinned { pinned ->
                    memcpy(pinned.addressOf(0), nsData.bytes, nsData.length)
                }
                result
            }
        }

        onResult(
            CaptureResult(
                latitude = lastLocation?.coordinate?.useContents { latitude },
                longitude = lastLocation?.coordinate?.useContents { longitude },
                photoBytes = bytes
            )
        )
        picker.dismissViewControllerAnimated(true, null)
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true, null)
    }

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        lastLocation = didUpdateLocations.lastOrNull() as? CLLocation
    }
}

@Composable
actual fun rememberHardwareManager(onResult: (CaptureResult) -> Unit): HardwareManager {
    return remember(onResult) { IosHardwareManager(onResult) }
}

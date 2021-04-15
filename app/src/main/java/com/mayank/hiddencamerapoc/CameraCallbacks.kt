package com.mayank.hiddencamerapoc

import com.mayank.hiddencamerapoc.CameraError.CameraErrorCodes
import java.io.File

internal interface CameraCallbacks {
    fun onImageCapture(imageFile: File)
    fun onCameraError(@CameraErrorCodes errorCode: Int)
}
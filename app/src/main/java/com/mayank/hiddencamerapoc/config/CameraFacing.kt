package com.mayank.hiddencamerapoc.config

import androidx.annotation.IntDef

class CameraFacing private constructor() {
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(REAR_FACING_CAMERA, FRONT_FACING_CAMERA)
    annotation class SupportedCameraFacing
    companion object {
        /**
         * Rear facing camera id.
         *
         * @see android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK
         */
        const val REAR_FACING_CAMERA = 0

        /**
         * Front facing camera id.
         *
         * @see android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT
         */
        const val FRONT_FACING_CAMERA = 1
    }

    init {
        throw RuntimeException("Cannot initialize this class.")
    }
}
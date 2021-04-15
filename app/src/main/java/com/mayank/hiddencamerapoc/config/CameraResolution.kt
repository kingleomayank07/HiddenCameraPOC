package com.mayank.hiddencamerapoc.config

import androidx.annotation.IntDef

class CameraResolution private constructor() {
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(HIGH_RESOLUTION, MEDIUM_RESOLUTION, LOW_RESOLUTION)
    annotation class SupportedResolution
    companion object {
        /**
         * This will capture the image at the highest possible resolution. That means if the camera sensor
         * is of 13MP, output image will have resolution of 13MP.
         */
        const val HIGH_RESOLUTION = 2006

        /**
         * This will capture the image at the medium resolution. That means if the camera sensor
         * is of 13MP, it will take image with resolution that is exact middle of the supported camera
         * resolutions ([Camera.Parameters.getSupportedPictureSizes]).
         */
        const val MEDIUM_RESOLUTION = 7895

        /**
         * This will capture the image at the lowest possible resolution. That means if the camera sensor
         * supports minimum 2MP, output image will have resolution of 2MP.
         */
        const val LOW_RESOLUTION = 7821
    }

    init {
        throw RuntimeException("Cannot initiate CameraResolution.")
    }
}
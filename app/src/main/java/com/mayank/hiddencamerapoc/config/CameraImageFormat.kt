package com.mayank.hiddencamerapoc.config

import androidx.annotation.IntDef

class CameraImageFormat private constructor() {
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(FORMAT_JPEG, FORMAT_PNG, FORMAT_WEBP)
    annotation class SupportedImageFormat
    companion object {
        /**
         * Image format for .jpg/.jpeg.
         */
        const val FORMAT_JPEG = 849

        /**
         * Image format for .png.
         */
        const val FORMAT_PNG = 545

        /**
         * Image format for .png.
         */
        const val FORMAT_WEBP = 563
    }

    init {
        throw RuntimeException("Cannot initialize CameraImageFormat.")
    }
}
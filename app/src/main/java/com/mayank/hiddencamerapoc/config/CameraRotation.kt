package com.mayank.hiddencamerapoc.config

import androidx.annotation.IntDef

class CameraRotation private constructor() {

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(ROTATION_0, ROTATION_90, ROTATION_180, ROTATION_270)
    annotation class SupportedRotation
    companion object {
        /**
         * Rotate image by 90 degrees.
         */
        const val ROTATION_90 = 90

        /**
         * Rotate image by 180 degrees.
         */
        const val ROTATION_180 = 180

        /**
         * Rotate image by 270 (or -90) degrees.
         */
        const val ROTATION_270 = 270

        /**
         * Don't rotate the image.
         */
        const val ROTATION_0 = 0
    }

    init {
        throw RuntimeException("Cannot initialize this class.")
    }
}
package com.mayank.hiddencamerapoc.config

import androidx.annotation.IntDef

class CameraFocus private constructor() {
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(AUTO, CONTINUOUS_PICTURE, NO_FOCUS)
    annotation class SupportedCameraFocus
    companion object {
        /**
         * Camera should focus automatically. This is the default focus mode if the camera focus
         * is not set.
         *
         * @see Camera.Parameters.FOCUS_MODE_AUTO
         */
        const val AUTO = 0

        /**
         * Camera should focus automatically.
         *
         * @see Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
         */
        const val CONTINUOUS_PICTURE = 1

        /**
         * Do not focus the camera.
         */
        const val NO_FOCUS = 2
    }

    init {
        throw RuntimeException("Cannot initialize this class.")
    }
}
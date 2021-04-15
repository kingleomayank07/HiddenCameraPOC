package com.mayank.hiddencamerapoc

import android.content.Context
import android.hardware.Camera
import com.mayank.hiddencamerapoc.HiddenCameraUtils.getCacheDir
import com.mayank.hiddencamerapoc.config.*
import com.mayank.hiddencamerapoc.config.CameraFacing.SupportedCameraFacing
import com.mayank.hiddencamerapoc.config.CameraFocus.SupportedCameraFocus
import com.mayank.hiddencamerapoc.config.CameraImageFormat.SupportedImageFormat
import com.mayank.hiddencamerapoc.config.CameraResolution.SupportedResolution
import com.mayank.hiddencamerapoc.config.CameraRotation.SupportedRotation
import java.io.File


class CameraConfig {
    private var mContext: Context? = null

    @get:SupportedResolution
    @SupportedResolution
    var resolution = CameraResolution.MEDIUM_RESOLUTION
        private set

    @get:SupportedCameraFacing
    @SupportedCameraFacing
    var facing = CameraFacing.REAR_FACING_CAMERA
        private set

    @get:SupportedImageFormat
    @SupportedImageFormat
    var imageFormat = CameraImageFormat.FORMAT_JPEG
        private set

    @get:SupportedRotation
    @SupportedRotation
    var imageRotation = CameraRotation.ROTATION_0
        private set

    @SupportedCameraFocus
    private var mCameraFocus = CameraFocus.AUTO
    var imageFile: File? = null
        private set

    fun getBuilder(context: Context?): Builder {
        mContext = context
        return Builder()
    }

    val focusMode: String?
        get() = when (mCameraFocus) {
            CameraFocus.AUTO -> Camera.Parameters.FOCUS_MODE_AUTO
            CameraFocus.CONTINUOUS_PICTURE -> Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            CameraFocus.NO_FOCUS -> null
            else -> throw RuntimeException("Invalid camera focus mode.")
        }

    inner class Builder {
        /**
         * Set the resolution of the output camera image. If you don't specify any resolution,
         * default image resolution will set to [CameraResolution.MEDIUM_RESOLUTION].
         *
         * @param resolution Any resolution from:
         *  * [CameraResolution.HIGH_RESOLUTION]
         *  * [CameraResolution.MEDIUM_RESOLUTION]
         *  * [CameraResolution.LOW_RESOLUTION]
         * @return [Builder]
         * @see CameraResolution
         */
        fun setCameraResolution(@SupportedResolution resolution1: Int): Builder {

            //Validate input
            if (resolution1 != CameraResolution.HIGH_RESOLUTION && resolution != CameraResolution.MEDIUM_RESOLUTION && resolution != CameraResolution.LOW_RESOLUTION) {
                throw RuntimeException("Invalid camera resolution.")
            }
            resolution = resolution1
            return this
        }

        /**
         * Set the camera facing with which you want to capture image.
         * Either rear facing camera or front facing camera. If you don't provide any camera facing,
         * default camera facing will be [CameraFacing.FRONT_FACING_CAMERA].
         *
         * @param cameraFacing Any camera facing from:
         *  * [CameraFacing.REAR_FACING_CAMERA]
         *  * [CameraFacing.FRONT_FACING_CAMERA]
         * @return [Builder]
         * @see CameraFacing
         */
        fun setCameraFacing(@SupportedCameraFacing cameraFacing: Int): Builder {
            //Validate input
            if (cameraFacing != CameraFacing.REAR_FACING_CAMERA &&
                cameraFacing != CameraFacing.FRONT_FACING_CAMERA
            ) {
                throw RuntimeException("Invalid camera facing value.")
            }
            facing = cameraFacing
            return this
        }

        /**
         * Set the camera focus mode. If you don't provide any camera focus mode,
         * default focus mode will be [CameraFocus.AUTO].
         *
         * @param focusMode Any camera focus mode from:
         *  * [CameraFocus.AUTO]
         *  * [CameraFocus.CONTINUOUS_PICTURE]
         *  * [CameraFocus.NO_FOCUS]
         * @return [Builder]
         * @see CameraFacing
         */
        fun setCameraFocus(@SupportedCameraFocus focusMode: Int): Builder {
            //Validate input
            if (focusMode != CameraFocus.AUTO && focusMode != CameraFocus.CONTINUOUS_PICTURE && focusMode != CameraFocus.NO_FOCUS) {
                throw RuntimeException("Invalid camera focus mode.")
            }
            mCameraFocus = focusMode
            return this
        }

        /**
         * Specify the image format for the output image. If you don't specify any output format,
         * default output format will be [CameraImageFormat.FORMAT_JPEG].
         *
         * @param imageFormat Any supported image format from:
         *  * [CameraImageFormat.FORMAT_JPEG]
         *  * [CameraImageFormat.FORMAT_PNG]
         * @return [Builder]
         * @see CameraImageFormat
         */
        fun setImageFormat(@SupportedImageFormat imageFormat1: Int): Builder {
            //Validate input
            if (imageFormat != CameraImageFormat.FORMAT_JPEG &&
                imageFormat != CameraImageFormat.FORMAT_PNG
            ) {
                throw RuntimeException("Invalid output image format.")
            }
            imageFormat = imageFormat1
            return this
        }

        /**
         * Specify the output image rotation. The output image will be rotated by amount of degree specified
         * before stored to the output file. By default there is no rotation applied.
         *
         * @param rotation Any supported rotation from:
         *  * [CameraRotation.ROTATION_0]
         *  * [CameraRotation.ROTATION_90]
         *  * [CameraRotation.ROTATION_180]
         *  * [CameraRotation.ROTATION_270]
         * @return [Builder]
         * @see CameraRotation
         */
        fun setImageRotation(@SupportedRotation rotation: Int): Builder {
            //Validate input
            if (rotation != CameraRotation.ROTATION_0 && rotation != CameraRotation.ROTATION_90 && rotation != CameraRotation.ROTATION_180 && rotation != CameraRotation.ROTATION_270) {
                throw RuntimeException("Invalid image rotation.")
            }
            imageRotation = rotation
            return this
        }

        /**
         * Set the location of the out put image. If you do not set any file for the output image, by
         * default image will be stored in the application's cache directory.
         *
         * @param imageFile [File] where you want to store the image.
         * @return [Builder]
         */
        fun setImageFile(imageFile1: File?): Builder {
            imageFile = imageFile1
            return this
        }

        /**
         * Build the configuration.
         *
         * @return [com.mayank.hiddencamerapoc.CameraConfig]
         */
        fun build(): CameraConfig {
            if (imageFile == null) imageFile = defaultStorageFile
            return this@CameraConfig
        }//IMG_214515184113123.png

        /**
         * Get the new file to store the image if there isn't any custom file location available.
         * This will create new file into the cache directory of the application.
         */
        private val defaultStorageFile: File
            get() = File(
                getCacheDir(mContext!!).absolutePath
                        + File.separator
                        + "IMG_" + System.currentTimeMillis() //IMG_214515184113123.png
                        + if (imageFormat == CameraImageFormat.FORMAT_JPEG) ".jpeg" else ".png"
            )
    }
}
package com.mayank.hiddencamerapoc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Matrix
import android.hardware.Camera
import android.os.Build
import android.provider.Settings
import androidx.annotation.WorkerThread
import com.mayank.hiddencamerapoc.config.CameraImageFormat
import com.mayank.hiddencamerapoc.config.CameraImageFormat.SupportedImageFormat
import com.mayank.hiddencamerapoc.config.CameraRotation.SupportedRotation
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object HiddenCameraUtils {
    @JvmStatic
    @SuppressLint("NewApi")
    fun canOverDrawOtherApps(context: Context?): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)
    }

    /**
     * This will open settings screen to allow the "Draw over other apps" permission to the application.
     *
     * @param context instance of caller.
     */
    @JvmStatic
    fun openDrawOverPermissionSetting(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * Get the cache directory.
     *
     * @param context instance of the caller
     * @return cache directory file.
     */
    @JvmStatic
    fun getCacheDir(context: Context): File {
        return if (context.externalCacheDir == null) context.cacheDir else context.externalCacheDir!!
    }

    /**
     * Check if the device has front camera or not?
     *
     * @param context context
     * @return true if the device has front camera.
     */
    @JvmStatic
    fun isFrontCameraAvailable(context: Context): Boolean {
        val numCameras = Camera.getNumberOfCameras()
        return numCameras > 0 && context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)
    }

    /**
     * Rotate the bitmap by 90 degree.
     *
     * @param bitmap original bitmap
     * @return rotated bitmap
     */
    @JvmStatic
    @WorkerThread
    fun rotateBitmap(bitmap: Bitmap, @SupportedRotation rotation: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotation.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    /**
     * Save image to the file.
     *
     * @param bitmap     bitmap to store.
     * @param fileToSave file where bitmap should stored
     */
    @JvmStatic
    fun saveImageFromFile(
        bitmap: Bitmap,
        fileToSave: File,
        @SupportedImageFormat imageFormat: Int
    ): Boolean {
        var out: FileOutputStream? = null
        var isSuccess: Boolean

        //Decide the image format
        val compressFormat: CompressFormat = when (imageFormat) {
            CameraImageFormat.FORMAT_JPEG -> CompressFormat.JPEG
            CameraImageFormat.FORMAT_WEBP -> CompressFormat.WEBP
            CameraImageFormat.FORMAT_PNG -> CompressFormat.PNG
            else -> CompressFormat.PNG
        }
        try {
            if (!fileToSave.exists()) fileToSave.createNewFile()
            out = FileOutputStream(fileToSave)
            bitmap.compress(compressFormat, 100, out) // bmp is your Bitmap instance
            isSuccess = true
        } catch (e: Exception) {
            e.printStackTrace()
            isSuccess = false
        } finally {
            try {
                out?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return isSuccess
    }
}
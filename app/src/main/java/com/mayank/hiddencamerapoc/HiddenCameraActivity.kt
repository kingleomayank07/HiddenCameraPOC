package com.mayank.hiddencamerapoc

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Camera
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.mayank.hiddencamerapoc.config.CameraFacing
import java.io.File
import java.io.IOException
import java.util.*


abstract class HiddenCameraActivity : AppCompatActivity(), CameraCallbacks {
    private var mCameraPreview: CameraPreview? = null
    private var mCamera: Camera? = null
    private var mMediaRecorder: MediaRecorder? = null
    public var isRecording = false
    private var mCachedCameraConfig: CameraConfig? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Add the camera preview surface to the root of the activity view.
        mCameraPreview = addPreView()
    }

    override fun onDestroy() {
        super.onDestroy()

        //stop preview and release the camera.
        stopCamera()
    }

    /**
     * Start the hidden camera. Make sure that you check for the runtime permissions before you start
     * the camera.
     *
     * @param cameraConfig camera configuration [CameraConfig]
     */
    @RequiresPermission(Manifest.permission.CAMERA)
    protected fun startCamera(cameraConfig: CameraConfig) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) { //check if the camera permission is available
            onCameraError(CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE)
        } else if (cameraConfig.facing == CameraFacing.FRONT_FACING_CAMERA
            && !HiddenCameraUtils.isFrontCameraAvailable(this)
        ) {   //Check if for the front camera
            onCameraError(CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA)
        } else {
            mCachedCameraConfig = cameraConfig
            mCameraPreview!!.startCameraInternal(cameraConfig)
        }
    }

    /**
     * Call this method to capture the image using the camera you initialized. Don't forget to
     * initialize the camera using [.startCamera] before using this function.
     */
    protected fun takePicture() {
        if (mCameraPreview != null) {
            if (mCameraPreview!!.isSafeToTakePictureInternal) {
                mCameraPreview!!.takePictureInternal()
            }
        } else {
            throw RuntimeException("Background camera not initialized. Call startCamera() to initialize the camera.")
        }
    }

    fun startRecording(): Boolean {
        if (isRecording) {
            mMediaRecorder?.stop()
            mMediaRecorder?.reset()
            mMediaRecorder?.release()
            isRecording = false
            return false
        }
        mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT)
        mCamera?.startPreview()
        mMediaRecorder = MediaRecorder()

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera?.unlock()
        mMediaRecorder?.setCamera(mCamera)

        // Step 2: Set sources
        mMediaRecorder?.setAudioSource(MediaRecorder.AudioSource.CAMCORDER)
        mMediaRecorder?.setVideoSource(MediaRecorder.VideoSource.CAMERA)

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder?.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_1080P))

        // Step 4: Set output file
//        mMediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

        val dateTime = Date()
        val file = File(getExternalFilesDir(null), "${dateTime.time}${dateTime.seconds}.mp4")
        mMediaRecorder?.setOutputFile(file.absolutePath)

        // Step 5: Set the preview output
//        mMediaRecorder?.setCaptureRate(60.0)
        mMediaRecorder?.setOrientationHint(180)
        mMediaRecorder?.setPreviewDisplay(mCameraPreview?.holder?.surface)

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder?.prepare()
            mMediaRecorder?.start()
            isRecording = true

        } catch (e: IllegalStateException) {
            Log.d("TAG", "IllegalStateException preparing MediaRecorder: " + e.localizedMessage)
            releaseMediaRecorder()
            return false
        } catch (e: IOException) {
            Log.d("TAG", "IOException preparing MediaRecorder: " + e.message)
            releaseMediaRecorder()
        }
        return true
    }

    private fun releaseMediaRecorder() {
        mMediaRecorder?.release()
    }

    /**
     * Stop and release the camera forcefully.
     */
    private fun stopCamera() {
        mCachedCameraConfig = null //Remove config.
        if (mCameraPreview != null) mCameraPreview!!.stopPreviewAndFreeCamera()
    }

    /**
     * Add camera preview to the root of the activity layout.
     *
     * @return [CameraPreview] that was added to the view.
     */
    private fun addPreView(): CameraPreview {
        //create fake camera view
        val cameraSourceCameraPreview = CameraPreview(this, this)
        cameraSourceCameraPreview.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        val view = (window.decorView.rootView as ViewGroup).getChildAt(0)
        when (view) {
            is LinearLayout -> {
                val params = LinearLayout.LayoutParams(1, 1)
                view.addView(cameraSourceCameraPreview, params)
            }
            is RelativeLayout -> {
                val params = RelativeLayout.LayoutParams(1, 1)
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
                view.addView(cameraSourceCameraPreview, params)
            }
            is FrameLayout -> {
                val params = FrameLayout.LayoutParams(1, 1)
                view.addView(cameraSourceCameraPreview, params)
            }
            else -> {
                throw RuntimeException("Root view of the activity/fragment cannot be other than Linear/Relative/Frame layout")
            }
        }
        return cameraSourceCameraPreview
    }

    public override fun onResume() {
        super.onResume()
        if (mCachedCameraConfig != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                return
            }
            startCamera(mCachedCameraConfig!!)
        }
    }

    public override fun onPause() {
        super.onPause()
        if (mCameraPreview != null) mCameraPreview!!.stopPreviewAndFreeCamera()
    }
}
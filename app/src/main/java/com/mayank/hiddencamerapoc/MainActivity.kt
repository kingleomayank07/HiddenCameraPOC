package com.mayank.hiddencamerapoc

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.mayank.hiddencamerapoc.CameraError.CameraErrorCodes
import com.mayank.hiddencamerapoc.config.*
import java.io.File

class MainActivity : HiddenCameraActivity() {
    private var mCameraConfig: CameraConfig? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hidden_cam)
        mCameraConfig = CameraConfig()
            .getBuilder(this)
            .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
            .setCameraResolution(CameraResolution.HIGH_RESOLUTION)
            .setImageFormat(CameraImageFormat.FORMAT_JPEG)
            .setImageRotation(CameraRotation.ROTATION_270)
            .setCameraFocus(CameraFocus.AUTO)
            .build()

        setWebView()

        //Check for the camera permission for the runtime
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {

            //Start camera preview
            startCamera(mCameraConfig!!)
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
                REQ_CODE_CAMERA_PERMISSION
            )
        }

        //Take a picture
        findViewById<Button>(R.id.capture_btn).setOnClickListener { view: View? ->
            //Take picture using the camera without preview.
//            takePicture()
            startRecording()
            if (isRecording) {
                findViewById<Button>(R.id.capture_btn).text = "Stop Recording"
            } else {
                findViewById<Button>(R.id.capture_btn).text = "Capture video"
            }
        }
    }

    private fun setWebView() {
        val webview = findViewById<WebView>(R.id.webview)
        webview.webViewClient = WebViewClient()
        webview.settings.javaScriptEnabled = true
        webview.loadUrl("https://google.com")
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQ_CODE_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera(mCameraConfig!!)
            } else {
                Toast.makeText(this, "error_camera_permission_denied", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onImageCapture(imageFile: File) {

        // Convert file to bitmap.
        // Do something.
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.RGB_565
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options)

        //Display the image to the image view
//        (findViewById<View>(R.id.cam_prev) as ImageView).setImageBitmap(bitmap)
        Snackbar.make(
            (findViewById<View>(R.id.webview) as WebView),
            "Image Saved!", Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onCameraError(@CameraErrorCodes errorCode: Int) {
        when (errorCode) {
            CameraError.ERROR_CAMERA_OPEN_FAILED ->
                Toast.makeText(
                    this,
                    "Camera open failed. Probably because another application is using the camera",
                    Toast.LENGTH_LONG
                ).show()
            CameraError.ERROR_IMAGE_WRITE_FAILED ->
                Toast.makeText(
                    this,
                    "Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission",
                    Toast.LENGTH_LONG
                ).show()
            CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE ->
                Toast.makeText(
                    this,
                    "camera permission is not available Ask for the camera permission before initializing it",
                    Toast.LENGTH_LONG
                ).show()
            CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION ->
                HiddenCameraUtils.openDrawOverPermissionSetting(this)
            CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA -> Toast.makeText(
                this,
                "Display information dialog to the user with steps to grant Draw over other app permission for the app",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    companion object {
        private const val REQ_CODE_CAMERA_PERMISSION = 1253
    }
}
package com.example.framgiathanghuyhoang.democamenra_kotlin

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.StreamConfigurationMap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.TextureView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var cameraManager: CameraManager
    var cameraFacing: Int = 0
    lateinit var cameraId: String
    lateinit var backgroundThread: HandlerThread
    lateinit var backgroundHandler: Handler
    lateinit var stateCallback: CameraDevice.StateCallback
    lateinit var surfaceTextureListener: TextureView.SurfaceTextureListener
    var cameraDevice: CameraDevice? = null
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraFacing = CameraCharacteristics.LENS_FACING_BACK
        stateCallback = object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                cameraDevice = camera
                createPreviewSession()
            }

            override fun onDisconnected(camera: CameraDevice) {
                cameraDevice?.close()
                this@MainActivity.cameraDevice = null
            }

            override fun onError(camera: CameraDevice, error: Int) {
                cameraDevice?.close()
                this@MainActivity.cameraDevice = null
            }

        }
        surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {


            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                return false
            }

            override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                setupCamera()
                openCamera()
            }

        }
    }

    private fun createPreviewSession() {

    }

    override fun onStop() {
        super.onStop()
        closeCamera();
        closeBackgroundThead()
    }

    private fun closeBackgroundThead() {

    }

    private fun closeCamera() {

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onResume() {
        super.onResume()
        openBackgroundThread()
        if (texture_view.isAvailable) {
            setupCamera()
            openCamera()
        } else {
            texture_view.surfaceTextureListener = surfaceTextureListener
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {

            }
        } catch (e: CameraAccessException) {
            e.printStackTrace();
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun setupCamera() {
        try {
            for (cameraId in cameraManager.cameraIdList) {
                val cameraCharacteristic = cameraManager.getCameraCharacteristics(cameraId)
                if (cameraCharacteristic.get(CameraCharacteristics.LENS_FACING) == cameraFacing) {
                    val streamConfigurationMap: StreamConfigurationMap? =
                        cameraCharacteristic.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                    val previewSize = streamConfigurationMap?.getOutputSizes(SurfaceTexture::class.java)?.get(0)
                    this.cameraId = cameraId
                }
            }

        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun openBackgroundThread() {
        backgroundThread = HandlerThread("camera_background_thread")
        backgroundThread.start()
        backgroundHandler = Handler(backgroundThread.looper)
    }
}

package com.ptit.theeyes.viewModel

import android.content.Context
import android.util.Size
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.google.common.util.concurrent.ListenableFuture
import com.ptit.theeyes.view.CameraFragmentDirections
import timber.log.Timber

class CameraViewModel: BaseViewModel() {
    lateinit var imageCapture: ImageCapture
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var preview: Preview
    lateinit var camera: Camera
    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    val flashMode = MutableLiveData(ImageCapture.FLASH_MODE_OFF)

    init{
        bindImageCapture()
    }

    private fun bindImageCapture() {
        imageCapture = ImageCapture.Builder()
            .setTargetResolution(Size(720,1080))
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setFlashMode(flashMode.value!!)
            .build()
    }

    fun startCamera(previewView: PreviewView, context: Context, lifecycleOwner: LifecycleOwner){
        cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            bindImageCapture()

            bindCamera(lifecycleOwner)
        }, ContextCompat.getMainExecutor(context))
    }

    private fun bindCamera(lifecycleOwner: LifecycleOwner){
        try{
            cameraProvider.unbindAll()

            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
        } catch(exc: Exception) {
            Timber.e(exc, "Use case binding failed")
        }
    }

    fun changeFlash(lifecycleOwner: LifecycleOwner){
        if(camera.cameraInfo.hasFlashUnit()) {
            when (flashMode.value) {
                ImageCapture.FLASH_MODE_OFF -> {
                    flashMode.value = ImageCapture.FLASH_MODE_ON
                }

                ImageCapture.FLASH_MODE_ON -> {
                    flashMode.value = ImageCapture.FLASH_MODE_AUTO
                }

                ImageCapture.FLASH_MODE_AUTO -> {
                    flashMode.value = ImageCapture.FLASH_MODE_OFF
                }
            }
            bindImageCapture()
            bindCamera(lifecycleOwner)
        }
    }

    fun navigateToPreview(imageUri: String) =
        navigate(CameraFragmentDirections.actionCameraFragmentToPreviewFragment(imageUri))
}
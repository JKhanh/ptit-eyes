package com.ptit.theeyes.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ptit.theeyes.R
import com.ptit.theeyes.databinding.FragmentCameraBinding
import com.ptit.theeyes.utils.Const
import com.ptit.theeyes.utils.getMediaDirectory
import com.ptit.theeyes.viewModel.BaseViewModel
import com.ptit.theeyes.viewModel.CameraViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment: BaseFragment() {
    private var _binding: FragmentCameraBinding? = null
    private val binding: FragmentCameraBinding get() = _binding!!

    private val viewModel: CameraViewModel by viewModel()
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        requireActivity().window.statusBarColor = Color.BLACK

        checkPermission()

        outputDirectory = requireActivity().getMediaDirectory(resources.getString(R.string.origin_folder))

        cameraExecutor = Executors.newSingleThreadExecutor()

        binding.shutter.setOnClickListener {
            takePhoto()
        }

        binding.buttonFlash.setOnClickListener {
            viewModel.changeFlash(this)
        }

        binding.preview.setOnTouchListener { _, event ->
            getZoomListener().onTouchEvent(event)
            when(event.action){
                 MotionEvent.ACTION_UP -> {
                     val factory = binding.preview.meteringPointFactory
                     val point = factory.createPoint(event.x, event.y)
                     val action = FocusMeteringAction.Builder(point).build()
                     viewModel.camera.cameraControl.startFocusAndMetering(action)
                 }
            }

            true
        }
    }

    private fun checkPermission() {
        if(isPermissionGranted()){
            viewModel.startCamera(binding.preview, requireContext(), viewLifecycleOwner)
        } else {
            requestPermission()
        }
    }

    private fun getZoomListener(): ScaleGestureDetector{
        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val currentZoomRatio = viewModel.camera.cameraInfo.zoomState.value?.zoomRatio ?: 0f
                val delta = detector.scaleFactor
                viewModel.camera.cameraControl.setZoomRatio(currentZoomRatio * delta)

                return true
            }
        }
        return ScaleGestureDetector(requireContext(), listener)
    }

    override fun getViewModel(): BaseViewModel = viewModel

    private fun takePhoto(){
        val imageCapture = viewModel.imageCapture

        val photoFile = File(
            outputDirectory,
            SimpleDateFormat(Const.FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis()) + ".jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    goToPreview(savedUri)
                }

                override fun onError(exc: ImageCaptureException) {
                    Timber.e("Photo capture failed: ${exc.message}")
                }

            }
        )
    }

    private fun goToPreview(savedUri: Uri?) {
        viewModel.navigateToPreview(savedUri.toString())
    }

    private fun isPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(isPermissionGranted()){
                viewModel.startCamera(binding.preview, requireContext(), viewLifecycleOwner)
            } else {
                Toast.makeText(requireContext(),
                    "The Eyes need to have Camera permission to perform this feature",
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
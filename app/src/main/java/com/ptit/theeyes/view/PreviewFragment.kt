package com.ptit.theeyes.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.ptit.theeyes.databinding.FragmentPreviewBinding
import com.ptit.theeyes.model.DetectModel
import com.ptit.theeyes.viewModel.BaseViewModel
import com.ptit.theeyes.viewModel.DetectViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class PreviewFragment: BaseFragment() {
    private var _binding: FragmentPreviewBinding? = null
    private val binding: FragmentPreviewBinding get() = _binding!!
    private val viewModel: DetectViewModel by sharedViewModel()

    private val args: PreviewFragmentArgs by navArgs()
    private lateinit var imageUri: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPreviewBinding.inflate(inflater, container, false)
        imageUri = args.imageUri
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel.getHistory()
        Glide.with(this)
            .load(imageUri.toUri())
            .into(binding.image)

        binding.buttonDetect.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            viewModel.setOriginImage(imageUri, requireContext().contentResolver)
            viewModel.annotateImage(viewModel.createRequest())
                .addOnCompleteListener { task ->
                    binding.progressBar.visibility = View.GONE
                    if(task.isSuccessful){
                        var result = ""
                        for (label in task.result!!.asJsonArray[0].asJsonObject["labelAnnotations"].asJsonArray) {
                            val labelObj = label.asJsonObject
                            val text = labelObj["description"]
                            val entityId = labelObj["mid"]
                            val confidence = labelObj["score"]
                            val detectModel = DetectModel(text.asString, entityId.asString, confidence.asString)
                            viewModel.writeData(detectModel)
                            result += arrayListOf(text, entityId, confidence).joinToString(separator = "\n")
                            result += "\n"
                        }
                        buildDialog(result)
                    } else{
                        Timber.e(task.exception)
                        Toast.makeText(requireContext(), "Detect Failed! Please try again", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun buildDialog(result: String) {
        val builder = AlertDialog.Builder(requireActivity())

        val dialog = builder.setMessage(result)
            .setTitle("Detect Result")
            .setPositiveButton("Close") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    override fun getViewModel(): BaseViewModel = viewModel

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
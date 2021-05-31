package com.ptit.theeyes.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ptit.theeyes.databinding.FragmentMainBinding
import com.ptit.theeyes.viewModel.BaseViewModel
import com.ptit.theeyes.viewModel.MainViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class MainFragment: BaseFragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!
    private val viewModel: MainViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnCamera.setOnClickListener {
            viewModel.navigateToCamera()
        }

        binding.btnHistory.setOnClickListener {
            viewModel.navigateToHistory()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getViewModel(): BaseViewModel = viewModel
}
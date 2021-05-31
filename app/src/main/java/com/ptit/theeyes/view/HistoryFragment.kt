package com.ptit.theeyes.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.ptit.theeyes.databinding.FragmentHistoryBinding
import com.ptit.theeyes.view.detectmodel.DetectModelAdapter
import com.ptit.theeyes.viewModel.BaseViewModel
import com.ptit.theeyes.viewModel.DetectViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class HistoryFragment: BaseFragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding: FragmentHistoryBinding get() = _binding!!
    private val viewModel: DetectViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.executePendingBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getHistoryOnce()
        Timber.d(viewModel.historyList.value.toString())
        val adapter = DetectModelAdapter(viewLifecycleOwner)
        binding.rvHistory.apply {
            this.adapter = adapter
            this.layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getViewModel(): BaseViewModel = viewModel
}
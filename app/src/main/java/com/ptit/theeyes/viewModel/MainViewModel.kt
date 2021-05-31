package com.ptit.theeyes.viewModel

import com.ptit.theeyes.view.MainFragmentDirections

class MainViewModel: BaseViewModel() {
    fun navigateToCamera() =
        navigate(MainFragmentDirections.actionMainFragmentToCameraFragment())

    fun navigateToHistory() =
        navigate(MainFragmentDirections.actionMainFragmentToHistoryFragment())
}
package com.ptit.theeyes.view.detectmodel

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ptit.theeyes.model.DetectModel

@BindingAdapter("app:model")
fun setCourse(recyclerView: RecyclerView, entries: List<DetectModel>?){
    if(entries == null || recyclerView.adapter == null){
        return
    }
    (recyclerView.adapter as DetectModelAdapter).submitList(
        entries
    )
}
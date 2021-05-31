package com.ptit.theeyes.view.detectmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ptit.theeyes.databinding.ItemDetectModelBinding
import com.ptit.theeyes.model.DetectModel
import java.util.concurrent.Executors

class DetectModelAdapter(
    private val lifecycleOwner: LifecycleOwner
): ListAdapter<DetectModel, DetectModelAdapter.DetectedViewHolder>(
    AsyncDifferConfig.Builder(
        ModelDiffCallback()
    )
        .setBackgroundThreadExecutor(Executors.newSingleThreadExecutor()).build()
) {

    class DetectedViewHolder(
        private val lifecycleOwner: LifecycleOwner,
        private val binding: ItemDetectModelBinding
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(detectModel: DetectModel){
            binding.model = detectModel
            binding.lifecycleOwner = lifecycleOwner
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetectedViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemDetectModelBinding.inflate(layoutInflater, parent, false)
        return DetectedViewHolder(lifecycleOwner, binding)
    }

    override fun onBindViewHolder(holder: DetectedViewHolder, position: Int) {
        val model = getItem(position)
        holder.bind(model)
    }


}

class ModelDiffCallback: DiffUtil.ItemCallback<DetectModel>() {
    override fun areItemsTheSame(oldItem: DetectModel, newItem: DetectModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DetectModel, newItem: DetectModel): Boolean {
        return oldItem == newItem
    }

}

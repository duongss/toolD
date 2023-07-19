package com.dss.tooldocapplication.split.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dss.tooldocapplication.databinding.ItemPreviewFileBinding
import com.dss.tooldocapplication.gone
import com.dss.tooldocapplication.split.model.Image
import com.dss.tooldocapplication.split.model.SplitPdfFile
import com.dss.tooldocapplication.visible

class FolderSplitDirAdapter(private val onOpenImage: (Image) -> Unit) :
    BaseRecyclerViewAdapter<SplitPdfFile, ItemPreviewFileBinding>() {

    override fun providesItemViewBinding(parent: ViewGroup, viewType: Int) =
        ItemPreviewFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun bindData(
        binding: ItemPreviewFileBinding,
        data: SplitPdfFile,
        position: Int,
        context: Context
    ) {
        Glide.with(context)
            .load(data.icon)
            .into(binding.ivIcon)
        binding.ivDrop.isActivated = data.isSelected
        binding.tevName.text = data.name

        val adapter = DirImageSplitAdapter {
            onOpenImage(it)
        }
        adapter.set(data.images)
        binding.rcvImage.adapter = adapter

        if (data.isSelected) {
            binding.rcvImage.visible()
        } else {
            binding.rcvImage.gone()
        }

        binding.cslGroup.setOnClickListener {
            data.isSelected = !data.isSelected
            notifyItemChanged(position)
        }
    }

}
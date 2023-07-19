package com.dss.tooldocapplication.split.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.dss.tooldocapplication.databinding.ItemImageBinding
import com.dss.tooldocapplication.split.model.Image

class DirImageSplitAdapter(private val onClick: (Image) -> Unit) :
    BaseRecyclerViewAdapter<Image, ItemImageBinding>() {

    override fun providesItemViewBinding(parent: ViewGroup, viewType: Int) =
        ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun bindData(
        binding: ItemImageBinding,
        data: Image,
        position: Int,
        context: Context
    ) {
        Glide.with(context).load(data.bitmap)
            .into(binding.imgDoc)

        binding.tvNumber.text = data.id.toString()

        binding.root.setOnClickListener {
            onClick(data)
        }
    }
}
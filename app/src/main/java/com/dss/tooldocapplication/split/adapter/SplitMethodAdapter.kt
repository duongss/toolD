package com.dss.tooldocapplication.split.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import com.dss.tooldocapplication.databinding.ItemSplitBinding
import com.dss.tooldocapplication.invisible
import com.dss.tooldocapplication.split.model.Split
import com.dss.tooldocapplication.visible

class SplitMethodAdapter :
    BaseRecyclerViewAdapter<Split, ItemSplitBinding>(isItemClick = true) {

    override fun providesItemViewBinding(parent: ViewGroup, viewType: Int) =
        ItemSplitBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    override fun bindData(
        binding: ItemSplitBinding,
        data: Split,
        position: Int,
        context: Context
    ) {
        binding.tvTitle.text = data.title
        binding.tvContent.text = data.content
        binding.ivSelect.isActivated = data.isSelected

        if (position == dataList.size - 1) {
            binding.underline.invisible()
        } else {
            binding.underline.visible()
        }
    }
}
package com.dss.tooldocapplication.split.model

import android.os.Parcelable
import com.dss.tooldocapplication.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class SplitPdfFile(
    val icon: Int = R.drawable.ic_action_folder,
    val name: String,
    var size: Int = 0,
    var images: ArrayList<Image> = arrayListOf(),
    var isSelected: Boolean = false
) : Parcelable
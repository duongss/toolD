package com.dss.tooldocapplication.split.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Split(
    var title: String,
    var content: String,
    var type: Type,
    var fixedIndex: String = "0",
    var rangeCustom: ArrayList<Range> = arrayListOf(),
    var rangeRemove: ArrayList<Range> = arrayListOf(),
    var isSelected: Boolean = false
) : Parcelable {
    enum class Type {
        CUSTOM_RANGE,
        FIXED_RANGE,
        DELETE_PAGE,
        ALL_PAGE
    }
}
package com.dss.tooldocapplication.split.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Range(
    var name: String = "",
    var startIndex: Int = 0,
    var endIndex: Int = 0
) : Parcelable
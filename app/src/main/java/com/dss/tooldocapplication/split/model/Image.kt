package com.dss.tooldocapplication.split.model

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class Image(
    var id: Int = 0,
    val url: String = "",
    var uri: Uri? = null,
    var isSelect: Boolean = false,
    var bitmap: Bitmap? = null,
    var timeSelect: Long = 0L
) : Parcelable
package com.dss.tooldocapplication.split.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FolderFile(
    val icon: Int? = null,
    var name: Int? = null,
    val type: Type = Type.FILE,
    var ex: ArrayList<String> = arrayListOf(),
    var dirPath: String = "",
    var size: Int = 0,
    var subName: String = ""
) :
    Parcelable {
    enum class Type {
        PDF,
        WORD,
        EXCEL,
        POWERPOINT,
        IMAGE,
        TXT,
        ZIP,
        FILE
    }
}
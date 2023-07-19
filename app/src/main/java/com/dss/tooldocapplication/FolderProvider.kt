package com.dss.tooldocapplication

import com.dss.tooldocapplication.split.model.FolderFile
import java.io.File

object FolderProvider {


    fun fileToFolder(file: File): FolderFile {
        return FolderFile(
            icon = R.drawable.ic_action_folder,
            subName = file.nameWithoutExtension,
            type = FolderFile.Type.FILE,
            dirPath = file.path
        )
    }


}
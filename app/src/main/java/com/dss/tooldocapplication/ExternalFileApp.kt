package com.dss.tooldocapplication

import android.os.Environment
import java.io.File


object ExternalFileApp {

    const val SCREEN_SHOT_DIR = "ScreenShot"

    const val CONVERT_DIR = "Converted"
    const val WORD_CONVERT_DIR = "$CONVERT_DIR/WordFiles"
    const val PDF_CONVERT_DIR = "$CONVERT_DIR/PdfFiles"
    const val EXCEL_CONVERT_DIR = "$CONVERT_DIR/ExcelFiles"
    const val POWERPOINT_CONVERT_DIR = "$CONVERT_DIR/PowerpointFiles"
    const val TXT_CONVERT_DIR = "$CONVERT_DIR/TxtFiles"
    const val IMAGE_CONVERT_DIR = "$CONVERT_DIR/ImageFiles"
    const val ZIP_CONVERT_DIR = "$CONVERT_DIR/ZipFiles"

    //  val f = File(App.appContext().getExternalFilesDir(null), PDF_CONVERT_DIR)

    fun getDirectoryDocumentSnap(): File {
        return File(Environment.getExternalStorageDirectory(), SCREEN_SHOT_DIR)
    }

    fun getDirectoryImageConvert(): File {
        val f = File(Environment.getExternalStorageDirectory(), IMAGE_CONVERT_DIR)
        return f.createDir()
    }

    fun getDirectoryZipConvert(): File {
        val f = File(Environment.getExternalStorageDirectory(), ZIP_CONVERT_DIR)
        return f.createDir()
    }

    fun getDirectoryWordConvert(): File {
        val f = File(Environment.getExternalStorageDirectory(), WORD_CONVERT_DIR)
        return f.createDir()
    }

    fun getDirectoryPdfConvert(): File {
        val f = File(Environment.getExternalStorageDirectory(), PDF_CONVERT_DIR)
        return f.createDir()
    }

    fun getDirectoryExcelConvert(): File {
        val f = File(Environment.getExternalStorageDirectory(), EXCEL_CONVERT_DIR)
        return f.createDir()
    }

    fun getDirectoryPowerpointConvert(): File {
        val f = File(Environment.getExternalStorageDirectory(), POWERPOINT_CONVERT_DIR)
        return f.createDir()
    }

    fun getDirectoryTxTConvert(): File {
        val f = File(Environment.getExternalStorageDirectory(), TXT_CONVERT_DIR)
        return f.createDir()
    }

    fun autoCreateFolder(
        directory: File,
        nameFolder: String,
        extension: String = "",
        upName: Int = 0,
    ): File {
        val name = if (upName == 0) {
            ""
        } else {
            "($upName)"
        }
        var f = if (extension.isNotEmpty()) {
            File(directory, "$nameFolder$name.$extension")
        } else {
            File(directory, "$nameFolder$name")
        }
        if (f.exists()) {
            f = autoCreateFolder(directory, nameFolder, extension, upName.plus(1))
        } else {
            f.createDir()
        }
        return f
    }

    fun File.createDir(): File {
        if (!exists()) {
            mkdirs()
        } else if (!isDirectory && canWrite()) {
            delete()
            mkdirs()
        }
        return this
    }
}
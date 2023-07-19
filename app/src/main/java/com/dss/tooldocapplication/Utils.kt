package com.dss.tooldocapplication

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object Utils {
    fun saveBitmapToInternal(
        context: Context,
        bitmapImage: Bitmap,
        name: String = "Document_${System.currentTimeMillis()}.png",
        dir: File? = null
    ): File {
        val file = dir ?: File(context.internalFile(), name)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            fos?.close()
        }
        return file
    }

    fun Context.internalFile(): File {
        return File(this.filesDir.absolutePath).apply {
            if (!exists()) mkdir()
        }
    }
}
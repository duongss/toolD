package com.dss.tooldocapplication.split.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dss.tooldocapplication.ExternalFileApp
import com.dss.tooldocapplication.R
import com.dss.tooldocapplication.split.model.Image
import com.dss.tooldocapplication.split.model.Range
import com.dss.tooldocapplication.split.model.Split
import com.dss.tooldocapplication.split.model.SplitPdfFile
import com.tom_roush.pdfbox.multipdf.PDFMergerUtility
import com.tom_roush.pdfbox.multipdf.Splitter
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.rendering.PDFRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class SplitViewModel : ViewModel() {

    val isShowLoading = MutableLiveData<Boolean>()

    fun showLoading() {
        isShowLoading.postValue(true)
    }

    fun dismissLoading() {
        isShowLoading.postValue(false)
    }

    var filePath: String? = null

    var password = ""

    lateinit var inputFile: File

    lateinit var currentSplit: Split

    lateinit var directorySplit: File

    var numberOfPages = 0

    var listPdfSplitPreview = arrayListOf<SplitPdfFile>()

    val listBitmapPreview = arrayListOf<Bitmap>()

    var isLoadPreview = MutableLiveData(false)

    fun initData() = viewModelScope.launch {
        filePath?.let {
            inputFile = File(it)
            val pdDoc: PDDocument = PDDocument.load(inputFile, password)
            numberOfPages = pdDoc.numberOfPages
            withContext(Dispatchers.IO) {
                try {
                    val pfd =
                        ParcelFileDescriptor.open(inputFile, ParcelFileDescriptor.MODE_READ_ONLY)
                    val renderer = PdfRenderer(pfd)
                    val pagesCount = renderer.pageCount - 1
                    for (i in 0..pagesCount) {
                        val page = renderer.openPage(i)
                        val bitmap =
                            Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        listBitmapPreview.add(bitmap)
                        page.close()
                    }
                    renderer.close()
                    pfd.close()
                } catch (e: Exception) {
                    val pr = PDFRenderer(pdDoc)
                    for (pageNumber in 0 until pdDoc.numberOfPages) {
                        listBitmapPreview.add(pr.renderImage(pageNumber))
                    }
                    pdDoc.close()
                }

                withContext(Dispatchers.Main) {
                    isLoadPreview.value = true
                }
            }

        } ?: kotlin.run {
            withContext(Dispatchers.Main) {
                isLoadPreview.value = true
            }
        }

    }


    fun preview(result: (Boolean) -> Unit) {
        listPdfSplitPreview.clear()
        when (currentSplit.type) {
            Split.Type.CUSTOM_RANGE -> {
                if (validRange(currentSplit.rangeCustom)) {
                    previewPdfCustomRange()
                    result(true)
                } else {
                    result(false)
                }
            }
            Split.Type.FIXED_RANGE -> {
                if (currentSplit.fixedIndex.isNotEmpty()) {
                    previewFixRange()
                    result(true)
                } else {
                    result(false)
                }
            }
            Split.Type.DELETE_PAGE -> {
                if (validRange(currentSplit.rangeRemove)) {
                    previewPdfDeleteRange()
                    result(true)
                } else {
                    result(false)
                }
            }
            Split.Type.ALL_PAGE -> {
                previewPdfAll()
                result(true)
            }
        }
    }

    fun split(context: Context, result: (Boolean) -> Unit) = viewModelScope.launch {
        showLoading()
        withContext(Dispatchers.IO) {
            directorySplit = ExternalFileApp.autoCreateFolder(
                ExternalFileApp.getDirectoryPdfConvert(),
                context.getString(R.string.split) + "_" + inputFile.nameWithoutExtension
            )
            var isSuccess = false
            when (currentSplit.type) {
                Split.Type.CUSTOM_RANGE -> {
                    if (validRange(currentSplit.rangeCustom)) {
                        splitCustomRange()
                        isSuccess = true
                    } else {
                        isSuccess = false
                    }
                }
                Split.Type.FIXED_RANGE -> {
                    if (currentSplit.fixedIndex.isNotEmpty()) {
                        splitFixRange()
                        isSuccess = true
                    } else {
                        isSuccess = false
                    }
                }
                Split.Type.DELETE_PAGE -> {
                    if (validRange(currentSplit.rangeRemove)) {
                        splitPdfDeleteRange()
                        isSuccess = true
                    } else {
                        isSuccess = false
                    }
                }
                Split.Type.ALL_PAGE -> {
                    splitPdfAll()
                    isSuccess = true
                }
            }
            withContext(Dispatchers.Main) {
                dismissLoading()

                if (!isSuccess) {
                    directorySplit.delete()
                }
                result(isSuccess)
            }
        }
    }

    private fun splitPdfAll() {
        val document = PDDocument.load(inputFile, password)
        Splitter().split(document).forEachIndexed { index, pdDocument ->
            val fileOutput = File(
                directorySplit,
                "$index" + "_" + inputFile.nameWithoutExtension + "." + inputFile.extension
            )
            pdDocument.save(fileOutput)
            pdDocument.close()
        }
        document.close()
    }

    private fun splitPdfDeleteRange() {
        val document = PDDocument.load(inputFile, password)
        val listRemoveIndex = arrayListOf<Int>()
        currentSplit.rangeRemove.forEach {
            IntRange(it.startIndex - 1, it.endIndex - 1).forEach {
                listRemoveIndex.add(it)
            }
        }
        listRemoveIndex.toSet().toList().forEach {
            try {
                document.removePage(it)
            } catch (e: IndexOutOfBoundsException) {
                // no page in document
            }
        }
        val fileOutput = File(
            directorySplit,
            inputFile.nameWithoutExtension + "_" + "_remove" + "." + inputFile.extension
        )
        document.save(fileOutput)
        document.close()
    }

    private fun splitCustomRange() {
        val document = PDDocument.load(inputFile, password)

        currentSplit.rangeCustom.forEachIndexed { index, range ->
            val fileOutput = File(
                directorySplit,
                inputFile.nameWithoutExtension + "_" + range.startIndex + "-" + range.endIndex + "." + inputFile.extension
            )

            try {
                val splitter = Splitter()
                splitter.setStartPage(range.startIndex)
                splitter.setEndPage(range.endIndex)
                splitter.setSplitAtPage(range.endIndex - range.startIndex + 1)
                for (doc in splitter.split(document)) {
                    doc.save(fileOutput)
                    doc.close()
                }
            } catch (e: Exception) {
            }
        }
        document.close()
    }

    private fun splitFixRange() {
        val document = PDDocument.load(inputFile, password)

        val splitter = Splitter()
        val pdfMerger = PDFMergerUtility()
        splitter.split(document).chunked(currentSplit.fixedIndex.toInt())
            .forEachIndexed { index, list ->
                val fileOutput = File(
                    directorySplit,
                    inputFile.nameWithoutExtension + "_" + index + "." + inputFile.extension
                )
                val dstDoc = list[0]
                list.forEachIndexed { indexS, pdDocument ->
                    if (indexS > 0) {
                        pdfMerger.appendDocument(dstDoc, pdDocument)
                    }
                }
                dstDoc.save(fileOutput)
                dstDoc.close()
            }

        document.close()
    }

    fun checkSplit(): Boolean {
        return this::currentSplit.isInitialized
    }

    private fun previewPdfCustomRange() {
        currentSplit.rangeCustom.forEach {
            val splitPdfFile = SplitPdfFile(
                name = inputFile.nameWithoutExtension + "_" + it.startIndex.toString() + "-" + it.endIndex.toString()
            )
            listBitmapPreview.forEachIndexed { index, bitmap ->
                if (index >= it.startIndex - 1 && index <= it.endIndex - 1) {
                    splitPdfFile.images.add(Image(index + 1, bitmap = bitmap))
                }
            }
            listPdfSplitPreview.add(splitPdfFile)
        }
    }

    private fun previewFixRange() {
        listBitmapPreview.chunked(currentSplit.fixedIndex.toInt())
            .forEachIndexed { indexChunked, list ->
                val splitPdfFile = SplitPdfFile(
                    name = inputFile.nameWithoutExtension + "_fixed_$indexChunked"
                )
                list.forEachIndexed { index, bitmap ->
                    splitPdfFile.images.add(Image(index + 1, bitmap = bitmap))
                }
                listPdfSplitPreview.add(splitPdfFile)
            }
    }


    private fun previewPdfDeleteRange() {
        val listRemoveRange = arrayListOf<Int>()
        currentSplit.rangeRemove.forEach {
            IntRange(it.startIndex, it.endIndex).forEach {
                listRemoveRange.add(it)
            }
        }
        val splitPdfFile =
            SplitPdfFile(name = inputFile.nameWithoutExtension + "_remove_preview")
        listBitmapPreview.forEachIndexed { index, bitmap ->
            val id = index + 1
            var isAdd = true
            listRemoveRange.toSet().toList().forEach { idRemove ->
                if (id == idRemove) {
                    isAdd = false
                }
            }
            if (isAdd) {
                splitPdfFile.images.add(Image(id, bitmap = bitmap))
            }
        }

        listPdfSplitPreview.add(splitPdfFile)
    }

    private fun previewPdfAll() {
        listBitmapPreview.forEachIndexed { index, bitmap ->
            val id = index + 1
            val splitPdfFile =
                SplitPdfFile(
                    name = inputFile.nameWithoutExtension + "_" + id
                )
            splitPdfFile.images.add(Image(id, bitmap = bitmap))
            listPdfSplitPreview.add(splitPdfFile)
        }
    }

    fun validRange(list: ArrayList<Range>): Boolean {
        list.forEach { data ->
            if (data.endIndex < data.startIndex || data.startIndex < 1 || data.endIndex < 1 || data.endIndex > numberOfPages) {
                return false
            }
            if (currentSplit.type == Split.Type.DELETE_PAGE && data.endIndex == numberOfPages) {
                return false
            }
        }
        return true
    }

}
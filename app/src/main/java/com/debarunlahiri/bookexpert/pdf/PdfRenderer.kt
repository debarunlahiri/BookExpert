package com.debarunlahiri.bookexpert.pdf

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

data class PageDimension(val width: Int, val height: Int) {
    fun heightByWidth(width: Int): Int = (height.toFloat() * width.toFloat() / this.width.toFloat()).toInt()
}

class PdfRender(private val fileDescriptor: ParcelFileDescriptor) {
    private val renderer = PdfRenderer(fileDescriptor)
    private val scope = CoroutineScope(Dispatchers.Default)
    private val mutex = Mutex()
    
    val pageCount: Int = renderer.pageCount
    
    val pages: List<Page> = List(pageCount) { Page(it) }
    
    fun close() {
        renderer.close()
        fileDescriptor.close()
    }
    
    inner class Page(private val index: Int) {
        private val _pageContent = MutableStateFlow<Bitmap?>(null)
        val pageContent: StateFlow<Bitmap?> = _pageContent
        
        private var isLoaded = false
        val dimension: PageDimension
        
        init {
            val page = renderer.openPage(index)
            dimension = PageDimension(page.width, page.height)
            page.close()
        }
        
        fun load() {
            if (isLoaded) return
            
            scope.launch {
                mutex.withLock {
                    if (!isLoaded) {
                        val bitmap = createBitmap()
                        _pageContent.value = bitmap
                        isLoaded = true
                    }
                }
            }
        }
        
        fun recycle() {
            _pageContent.value?.recycle()
            _pageContent.value = null
            isLoaded = false
        }
        
        private suspend fun createBitmap(): Bitmap = withContext(Dispatchers.Default) {
            val page = renderer.openPage(index)
            
            val bitmap = Bitmap.createBitmap(
                page.width,
                page.height,
                Bitmap.Config.ARGB_8888
            )
            
            page.render(
                bitmap,
                null,
                null,
                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
            )
            
            page.close()
            bitmap
        }
    }
} 
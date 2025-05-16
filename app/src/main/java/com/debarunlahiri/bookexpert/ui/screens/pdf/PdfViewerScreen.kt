package com.debarunlahiri.bookexpert

import android.graphics.Bitmap
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.debarunlahiri.bookexpert.pdf.PdfRender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun PdfViewerScreen(onBackClick: () -> Unit = {}) {
    val pdfUrl = "https://fssservices.bookxpert.co/GeneratedPDF/Companies/nadc/2024-2025/BalanceSheet.pdf"
    val context = LocalContext.current
    
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var pdfRender by remember { mutableStateOf<PdfRender?>(null) }
    
    LaunchedEffect(pdfUrl) {
        try {
            val tempFile = withContext(Dispatchers.IO) {
                // Download PDF file
                val fileName = "temp_pdf_${System.currentTimeMillis()}.pdf"
                val file = File(context.cacheDir, fileName)
                
                val url = URL(pdfUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.connect()
                
                val inputStream = connection.inputStream
                val outputStream = FileOutputStream(file)
                
                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                
                file
            }
            
            val fileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRender = PdfRender(fileDescriptor)
            isLoading = false
        } catch (e: Exception) {
            error = "Failed to load PDF: ${e.message}"
            isLoading = false
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp)
                )
            }
            error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Error loading PDF",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = error ?: "Unknown error",
                        modifier = Modifier.padding(top = 8.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            pdfRender != null -> {
                PdfReader(pdfRender = pdfRender!!)
            }
        }
    }
}

@Composable
fun PdfReader(pdfRender: PdfRender) {
    val listState = rememberLazyListState()
    
    DisposableEffect(pdfRender) {
        onDispose {
            pdfRender.close()
        }
    }
    
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(pdfRender.pages) { _, page ->
                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val pageContent by page.pageContent.collectAsState()
                    val maxWidth = maxWidth
                    
                    DisposableEffect(page) {
                        page.load()
                        onDispose {
                            page.recycle()
                        }
                    }
                    
                    if (pageContent != null) {
                        Image(
                            bitmap = pageContent!!.asImageBitmap(),
                            contentDescription = "PDF Page",
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        // Show placeholder with correct aspect ratio
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(maxWidth.value.dp.times(page.dimension.height.toFloat() / page.dimension.width.toFloat()))
                                .background(Color.LightGray)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
        
        // Show page counter
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            val currentPage = (listState.firstVisibleItemIndex + 1).coerceAtMost(pdfRender.pageCount)
            
            Text(
                text = "Page $currentPage of ${pdfRender.pageCount}",
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White
            )
        }
    }
}
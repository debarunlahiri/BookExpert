package com.debarunlahiri.bookexpert

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ImageHandlerScreen() {
    val context = LocalContext.current
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        imageBitmap = bitmap
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        isLoading = true
        coroutineScope.launch {
            try {
                uri?.let { 
                    val bitmap = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(it)?.use { 
                            BitmapFactory.decodeStream(it) 
                        }
                    }
                    imageBitmap = bitmap
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) cameraLauncher.launch(null)
        else Toast.makeText(context, "Camera permission is needed to capture images", Toast.LENGTH_SHORT).show()
    }
    
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.READ_MEDIA_IMAGES] == true
        } else {
            permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true || 
            permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
        }
        
        if (granted) {
            saveImage(context, imageBitmap, coroutineScope) { isLoading = it }
        } else {
            Toast.makeText(context, "Storage permission is needed to save images", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
        } else {
            imageBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(), 
                    contentDescription = "Selected Image", 
                    modifier = Modifier.size(300.dp)
                )
            } ?: run {
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No image selected")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraLauncher.launch(null)
                } else {
                    cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }) {
                Text("Capture Image")
            }
            
            Button(onClick = { galleryLauncher.launch("image/*") }) {
                Text("Select from Gallery")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { imageBitmap = null },
                enabled = imageBitmap != null
            ) {
                Text("Clear Image")
            }
            
            Button(
                onClick = {
                    if (imageBitmap != null) {
                        val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
                        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        } else {
                            // For Android 10+ (API 29+), we don't need runtime permissions for saving to MediaStore
                            arrayOf()
                        }
                        
                        if (permissionsToRequest.isEmpty() || permissionsToRequest.all { 
                                ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED 
                            }) {
                            saveImage(context, imageBitmap, coroutineScope) { isLoading = it }
                        } else {
                            storagePermissionLauncher.launch(permissionsToRequest)
                        }
                    }
                },
                enabled = imageBitmap != null
            ) {
                Text("Save Image")
            }
        }
    }
}

private fun saveImage(
    context: android.content.Context,
    bitmap: Bitmap?,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    updateLoadingState: (Boolean) -> Unit
) {
    bitmap?.let {
        updateLoadingState(true)
        coroutineScope.launch {
            try {
                saveImageToGallery(context, it)
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Image saved to gallery", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Failed to save image: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                updateLoadingState(false)
            }
        }
    }
}

private suspend fun saveImageToGallery(context: android.content.Context, bitmap: Bitmap) {
    withContext(Dispatchers.IO) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val filename = "BookExpert_$timestamp.jpg"
        
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
        }
        
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        uri?.let { 
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                    throw Exception("Failed to save bitmap")
                }
            } ?: throw Exception("Failed to open output stream")
        } ?: throw Exception("Failed to create new MediaStore record")
    }
}
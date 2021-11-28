package com.example.photosearch.viewmodels

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photosearch.repository.Repository
import com.example.photosearch.data.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class FullViewViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    fun isExists(photoId: String) = repository.isExists(photoId)

    fun save(photo: Photo) {
        viewModelScope.launch {
            repository.save(photo)
        }
    }

    fun delete(photo: Photo) {
        viewModelScope.launch {
            repository.delete(photo)
        }
    }

    suspend fun savePhotoToExternalStorage(displayName: String,
                                                   bmp: Bitmap,
                                                   fragmentContext: Context): Boolean {
        return withContext(Dispatchers.IO) {
            val imageCollection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL_PRIMARY
                    )
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$displayName.jpg")
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.WIDTH, bmp.width)
                put(MediaStore.Images.Media.HEIGHT, bmp.height)
            }
            try {
                fragmentContext.contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                    fragmentContext.contentResolver.openOutputStream(uri).use { outputStream ->
                        if(!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                            throw IOException("Couldn't save bitmap")
                        }
                    }
                } ?: throw IOException("Couldn't create MediaStore entry")
                true
            } catch(e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }
}
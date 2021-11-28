package com.example.photosearch.viewmodels

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.example.photosearch.data.StoragePhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class GalleryViewModel: ViewModel() {

    suspend fun deletePhotoFromInternalStorage(uri: Uri) {
        return withContext(Dispatchers.IO) {
            val file = File(uri.path!!)
            try {
                file.delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun loadFromExternalStorage(fragmentContext: Context): List<StoragePhoto> {
        return withContext(Dispatchers.IO) {
            val photos = mutableListOf<StoragePhoto>()

            val collection =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    MediaStore.Images.Media.getContentUri(
                        MediaStore.VOLUME_EXTERNAL
                    )
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
            )
            val selection = null
            val selectionArgs = null
            val sortOrder = null

            fragmentContext.contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    photos.add(StoragePhoto(name, contentUri))
                }
                photos.toList()
            }?: listOf()
        }
    }

    suspend fun loadInternalStorage(fragmentContext: Context): List<StoragePhoto> {
        return withContext(Dispatchers.IO) {
            val files = fragmentContext.getDir("images", Context.MODE_PRIVATE).listFiles()
            files?.map {
                val bytes = it.readBytes()
                val uri = it.toUri()
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                StoragePhoto(it.name, uri)
            } ?: listOf()
        }
    }

    suspend fun deletePhotoFromExternalStorage(photoUri: Uri,
                                                       fragmentActivity: FragmentActivity,
                                                       intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        withContext(Dispatchers.IO) {
            try {
                fragmentActivity.contentResolver.delete(photoUri, null, null)
            } catch (e: SecurityException) {
                val intentSender = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                        MediaStore.createDeleteRequest(fragmentActivity.contentResolver, listOf(photoUri)).intentSender
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        val recoverableSecurityException = e as? RecoverableSecurityException
                        recoverableSecurityException?.userAction?.actionIntent?.intentSender
                    }
                    else -> null
                }
                intentSender?.let { sender ->
                    intentSenderLauncher.launch(
                        IntentSenderRequest.Builder(sender).build()
                    )
                }
            }
        }
    }
}
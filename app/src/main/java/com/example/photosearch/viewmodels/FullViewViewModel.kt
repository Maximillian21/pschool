package com.example.photosearch.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photosearch.api.Repository
import com.example.photosearch.data.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
}
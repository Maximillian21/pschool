package com.example.photosearch.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photosearch.api.Repository
import com.example.photosearch.data.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    lateinit var savedPhoto: LiveData<List<Photo>>

    fun getFavoritesPhotos(id: Int) {
        savedPhoto = repository.getAll(id)
    }

    fun removePhoto(photo: Photo) {
        viewModelScope.launch {
            repository.delete(photo)
        }
    }
}
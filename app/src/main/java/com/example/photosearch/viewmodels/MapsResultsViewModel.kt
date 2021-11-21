package com.example.photosearch.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photosearch.data.ApiResponse
import com.example.photosearch.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MapsResultsViewModel @Inject constructor(
    private val repository: Repository
): BaseResultsViewModel(){

    fun refresh(lat: Double, lon: Double) {
        fetchLinks(lat, lon)
    }

    private fun fetchLinks(lat: Double, lon: Double) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getByGeo(lat, lon)
            withContext(Dispatchers.Main) {
                if(response.isSuccessful) {
                    photosList.postValue(response.body())
                    loading.value = false
                    Log.d("MainViewModel", "success")
                }
                else {
                    Log.d("MainViewModel", response.message())
                    loading.value = false
                }
            }
        }
    }
}
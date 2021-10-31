package com.example.photosearch.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photosearch.api.RetrofitBuilder
import com.example.photosearch.data.ApiResponse
import kotlinx.coroutines.*

class MainViewModel : ViewModel() {
    private val service = RetrofitBuilder.apiService
    private var job: Job? = null
    val photosLinks = MutableLiveData<ApiResponse>()
    val loading = MutableLiveData<Boolean>()

    fun refresh(text: String) {
        fetchLinks(text)
    }

    private fun fetchLinks(text: String) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = service.getSearchResult(text)
            withContext(Dispatchers.Main) {
                if(response.isSuccessful) {
                    photosLinks.postValue(response.body())
                    loading.value = false
                    Log.d("result", "success")
                }
                else {
                    Log.d("error", response.message())
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
package com.example.photosearch.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photosearch.BaseApp
import com.example.photosearch.data.ApiResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.async

open class BaseResultsViewModel: ViewModel() {
    private val BASE_PHOTO_URL = "https://live.staticflickr.com/"

    protected var job: Job? = null
    val photosList = MutableLiveData<ApiResponse>()
    val loading = MutableLiveData<Boolean>()

    suspend fun setPhotoValues(apiResponse: ApiResponse, searchQuery: String) =
        viewModelScope.async{
            for(i in apiResponse.photos.photo) {
                i.searchText = searchQuery
                i.accountId = BaseApp.globalAccountId
                i.photoLink = BASE_PHOTO_URL + "${i.server}/${i.id}_${i.secret}_m.jpg"
            }
        }.await()

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
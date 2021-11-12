package com.example.photosearch.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photosearch.repository.Repository
import com.example.photosearch.data.ApiResponse
import com.example.photosearch.data.SearchHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val prefKey = "count"
    private val sharedPrefFile = "sharedPreference"
    lateinit var sharedPreferences: SharedPreferences

    private var job: Job? = null
    val photosLinks = MutableLiveData<ApiResponse>()
    val loading = MutableLiveData<Boolean>()
    lateinit var searchHistory: LiveData<List<SearchHistory>>

    fun refresh(text: String) {
        fetchLinks(text)
    }

    fun loadNextPage(text: String, page: Int) {
        fetchNewPage(text, page)
    }

    private fun fetchLinks(text: String) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getSearchResult(text)
            withContext(Dispatchers.Main) {
                if(response.isSuccessful) {
                    photosLinks.postValue(response.body())
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

    private fun fetchNewPage(text: String, page: Int) {
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getNewPage(text, page)
            withContext(Dispatchers.Main) {
                if(response.isSuccessful) {
                    photosLinks.postValue(response.body())
                    Log.d("MainViewModel", "success")
                }
                else {
                    Log.d("MainViewModel", response.message())
                }
            }
        }
    }

    fun getHistory(id: Int) {
        searchHistory = repository.getHistory(id)
    }


    suspend fun setPhotoValues(apiResponse: ApiResponse, searchText: String, accountId: Int) =
        viewModelScope.async{
        for(i in apiResponse.photos.photo) {
            i.searchText = searchText
            i.accountId = accountId
        }
    }.await()

    fun loadPref(context: Context): String {
        sharedPreferences = context.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        val count = sharedPreferences.getString(prefKey, "")
        return count.toString()
    }

    fun savePref(text: String) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(prefKey, text)
        editor.apply()
    }

    fun addQuery(text: String, accountId: Int) {
        viewModelScope.launch {
            val field = SearchHistory(text)
            field.accountId = accountId
            repository.addQuery(field)
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
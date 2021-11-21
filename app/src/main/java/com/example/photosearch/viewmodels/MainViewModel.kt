package com.example.photosearch.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.photosearch.repository.Repository
import com.example.photosearch.data.ApiResponse
import com.example.photosearch.data.SearchHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository
) : BaseResultsViewModel() {
    var pages: Int = 1
    private val prefKey = "history"
    private val sharedPrefFile = "sharedPreference"
    lateinit var sharedPreferences: SharedPreferences

    lateinit var searchHistory: LiveData<List<SearchHistory>>

    fun refresh(text: String) {
        fetchLinks(text)
    }

    private fun fetchLinks(text: String) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getSearchResult(text)
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

    private fun fetchNewPage(text: String, page: Int) {
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = repository.getNewPage(text, page)
            withContext(Dispatchers.Main) {
                if(response.isSuccessful) {
                    photosList.postValue(response.body())
                    Log.d("MainViewModel", "success")
                }
                else {
                    Log.d("MainViewModel", response.message())
                }
            }
        }
    }

    fun getOnScroll(layoutManager: RecyclerView.LayoutManager, text: String): RecyclerView.OnScrollListener {
        var loadingScroll = true
        var pastVisibleItems: Int
        var visibleItemCount: Int
        var totalItemCount: Int
        var page = 1

        return object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.childCount
                    totalItemCount = layoutManager.itemCount
                    pastVisibleItems =
                        ( layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
                    if (loadingScroll) {
                        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
                            if(++page <= pages) {
                                Log.d("MainViewModel page", page.toString())
                                Log.d("MainViewModel text", text)
                                fetchNewPage(text, page)
                            }
                            loadingScroll = true
                        }
                    }
                }
            }
        }
    }

    fun getHistory(id: Int) {
        searchHistory = repository.getHistory(id)
    }

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
}
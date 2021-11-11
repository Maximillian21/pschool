package com.example.photosearch.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photosearch.api.RetrofitBuilder
import com.example.photosearch.data.ApiResponse
import com.example.photosearch.data.Photo
import com.example.photosearch.views.WebViewActivity
import kotlinx.coroutines.*

class MainViewModel : ViewModel() {
    private val service = RetrofitBuilder.apiService
    private val baseUrlForString = RetrofitBuilder.BASE_URL
    private var job: Job? = null
    val photosLinks = MutableLiveData<ApiResponse>()
    val loading = MutableLiveData<Boolean>()

    fun refresh(text: String) {
        fetchLinks(text)
    }

    suspend fun createLinksString(linksList: List<Photo>, context: Context, bundle: Bundle?) =
        GlobalScope.async  {
            val resultSpannableText = SpannableStringBuilder()
            var index: Int
            for (element in linksList) {
                val totalString = baseUrlForString + element.owner + "/" + element.id
                resultSpannableText.appendLine(totalString)
                index = resultSpannableText.indexOf(totalString)
                val clickableSpan: ClickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        val intent = Intent(context, WebViewActivity::class.java)
                        intent.putExtra("url", Uri.parse(totalString).toString())
                        startActivity(context, intent, bundle)
                    }
                }
                resultSpannableText.setSpan(
                    clickableSpan, index, index + totalString.length,
                    SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            return@async resultSpannableText
        }.await()

    private fun fetchLinks(text: String) {
        loading.value = true
        job = CoroutineScope(Dispatchers.IO).launch {
            val response = service.getSearchResult(text)
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

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}
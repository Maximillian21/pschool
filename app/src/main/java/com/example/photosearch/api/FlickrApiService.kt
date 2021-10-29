package com.example.photosearch.api

import com.example.photosearch.data.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val API_KEY = "b07f3fcd2a168aa20e4f02a526e98499"

interface FlickrApiService {

    @GET("?method=flickr.photos.search&api_key=b07f3fcd2a168aa20e4f02a526e98499&format=json&nojsoncallback=1")
    suspend fun getSearchResult(
        @Query("text") text: String,
    ): Response<ApiResponse>
}
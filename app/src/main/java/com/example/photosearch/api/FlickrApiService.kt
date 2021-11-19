package com.example.photosearch.api

import com.example.photosearch.BuildConfig
import com.example.photosearch.data.ApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val key = BuildConfig.API_KEY

interface FlickrApiService {

    @GET("services/rest/?method=flickr.photos.search&api_key=$key&format=json&nojsoncallback=1")
    suspend fun getSearchResult(
        @Query("text") text: String,
    ): Response<ApiResponse>

    @GET("services/rest/?method=flickr.photos.search&api_key=$key&format=json&nojsoncallback=1")
    suspend fun getNewPage(
        @Query("text") text: String,
        @Query("page") page: Int
        ): Response<ApiResponse>

    @GET("services/rest/?method=flickr.photos.search&api_key=$key&format=json&nojsoncallback=1")
    suspend fun getByGeo(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<ApiResponse>
}
package com.example.photosearch.repository

import com.example.photosearch.api.FlickrApiService
import com.example.photosearch.data.Account
import com.example.photosearch.data.ApiResponse
import com.example.photosearch.data.Photo
import com.example.photosearch.data.SearchHistory
import com.example.photosearch.database.AccountDao
import com.example.photosearch.database.PhotoDao
import com.example.photosearch.database.SearchHistoryDao
import retrofit2.Response

class Repository(
    private val apiService: FlickrApiService,
    private val photoDao: PhotoDao,
    private val searchHistoryDao: SearchHistoryDao,
    private val accountDao: AccountDao
    ) {

    suspend fun getSearchResult(text: String): Response<ApiResponse> {
        return apiService.getSearchResult(text)
    }

    suspend fun getNewPage(text: String, page: Int): Response<ApiResponse> {
        return apiService.getNewPage(text, page)
    }

    suspend fun getByGeo(lat: Double, lon: Double): Response<ApiResponse> {
        return apiService.getByGeo(lat, lon)
    }

    fun isExists(photoId: String) = photoDao.isExists(photoId)

    fun getAll(id: Int) = photoDao.getAll(id)

    fun getHistory(accountId: Int) = searchHistoryDao.getHistory(accountId)

    fun isExistsAccount(login: String) = accountDao.isExistsAccount(login)

    fun getAccountId(login: String) = accountDao.getAccountId(login)

    suspend fun addAccount(account: Account) = accountDao.addAccount(account)

    suspend fun save(photo: Photo) = photoDao.save(photo)

    suspend fun addQuery(searchHistory: SearchHistory) = searchHistoryDao.addQuery(searchHistory)

    suspend fun delete(photo: Photo) = photoDao.delete(photo)
}
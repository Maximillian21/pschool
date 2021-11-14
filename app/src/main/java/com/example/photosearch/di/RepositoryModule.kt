package com.example.photosearch.di

import com.example.photosearch.api.FlickrApiService
import com.example.photosearch.repository.Repository
import com.example.photosearch.database.AccountDao
import com.example.photosearch.database.PhotoDao
import com.example.photosearch.database.SearchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideRepository(
        apiService: FlickrApiService,
        photoDao: PhotoDao,
        searchHistoryDao: SearchHistoryDao,
        accountDao: AccountDao
    ): Repository {
        return Repository(apiService, photoDao, searchHistoryDao, accountDao)
    }

}
package com.example.photosearch.di

import android.content.Context
import com.example.photosearch.database.AccountDao
import com.example.photosearch.database.LocalDatabase
import com.example.photosearch.database.PhotoDao
import com.example.photosearch.database.SearchHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): LocalDatabase {
        return LocalDatabase.create(context)
    }

    @Singleton
    @Provides
    fun providePhotoDao(database: LocalDatabase): PhotoDao {
        return database.photoDao()
    }

    @Singleton
    @Provides
    fun provideSearchHistoryDay(database: LocalDatabase): SearchHistoryDao {
        return database.searchHistoryDao()
    }

    @Singleton
    @Provides
    fun provideAccount(database: LocalDatabase): AccountDao {
        return database.accountDao()
    }
}
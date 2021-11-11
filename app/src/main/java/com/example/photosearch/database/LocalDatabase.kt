package com.example.photosearch.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.photosearch.data.Account
import com.example.photosearch.data.Photo
import com.example.photosearch.data.SearchHistory

@Database(
    entities = [Photo::class, SearchHistory::class, Account::class],
    version = 1
)
abstract class LocalDatabase: RoomDatabase() {

    abstract fun photoDao(): PhotoDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun accountDao(): AccountDao

    companion object {
        fun create(context: Context): LocalDatabase {
            return Room.databaseBuilder(context, LocalDatabase::class.java, "photos.db")
                .build()
        }
    }

}
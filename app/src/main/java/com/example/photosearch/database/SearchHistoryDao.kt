package com.example.photosearch.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.photosearch.data.SearchHistory

@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM SearchHistory WHERE accountId = :accountId")
    fun getHistory(accountId: Int): LiveData<List<SearchHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addQuery(searchHistory: SearchHistory)
}
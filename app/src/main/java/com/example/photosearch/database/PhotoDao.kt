package com.example.photosearch.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.photosearch.data.Photo

@Dao
interface PhotoDao {

    @Query("SELECT EXISTS(SELECT * FROM Photo WHERE id = :photoId)")
    fun isExists(photoId: String): LiveData<Boolean>

    @Query("UPDATE Photo SET accountId=:id")
    fun update(id: Int)

    @Query("SELECT * FROM Photo WHERE accountId=:id")
    fun getAll(id: Int): LiveData<List<Photo>>

    @Insert
    suspend fun save(photo: Photo)

    @Delete
    suspend fun delete(photo: Photo)
}
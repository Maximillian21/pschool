package com.example.photosearch.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.photosearch.data.Account

@Dao
interface AccountDao {

    @Query("SELECT EXISTS(SELECT * FROM Account WHERE login = :login)")
    fun isExistsAccount(login: String): LiveData<Boolean>

    @Query("SELECT * FROM Account WHERE login = :login")
    fun getAccountId(login: String): LiveData<Account>

    @Insert
    suspend fun addAccount(account: Account)
}
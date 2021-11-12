package com.example.photosearch.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.photosearch.repository.Repository
import com.example.photosearch.data.Account
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    lateinit var savedPhoto: LiveData<Account>

    fun setAccountId(login: String) = repository.getAccountId(login)

    fun isExists(login: String) = repository.isExistsAccount(login)

    suspend fun addAccount(account: Account) = viewModelScope.async {
        repository.addAccount(account)
        savedPhoto = setAccountId(account.login)
    }.await()
}
package com.example.photosearch.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SearchHistory(
    @PrimaryKey
    val query: String
) {
    var accountId: Int = 0
}

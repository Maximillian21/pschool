package com.example.photosearch.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Photo(
    val farm: Int,
    @PrimaryKey
    val id: String,
    val isfamily: Int,
    val isfriend: Int,
    val ispublic: Int,
    val owner: String,
    val secret: String,
    val server: String,
    val title: String,
    var searchText: String,
    var accountId: Int,
    var photoLink: String
): Parcelable
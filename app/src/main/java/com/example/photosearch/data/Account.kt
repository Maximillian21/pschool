package com.example.photosearch.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Account(
    val login: String
): Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

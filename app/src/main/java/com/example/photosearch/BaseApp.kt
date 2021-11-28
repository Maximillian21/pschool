package com.example.photosearch

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApp: Application() {
    companion object {
        @JvmField
        var globalAccountId: Int = 0
    }
}
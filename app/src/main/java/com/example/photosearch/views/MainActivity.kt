package com.example.photosearch.views

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.photosearch.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav)
        val navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener {_, destination, _ ->
            if(destination.id == R.id.loginFragment || destination.id == R.id.fullViewFragment || destination.id == R.id.mapsResultsFragment) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }
        }

        val appBarConfiguration = AppBarConfiguration(setOf(R.id.mainFragment, R.id.mapsFragment, R.id.favourites, R.id.galleryFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}


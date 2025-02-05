package com.example.surfriders

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.surfriders.data.location.LocationService
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("creation","creating ypi screen")
        setContentView(R.layout.activity_main)
        setUI()

    }

    private fun setUI () {
        fetchBeachData()
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(bottomNavigationView, navController)
    }

    fun fetchBeachData() {
        Log.d("BeachLocations", "Fetching beach data...")
        lifecycleScope.launch {
            LocationService.instance.getLocations()  // Call the suspend function
        }
    }
}
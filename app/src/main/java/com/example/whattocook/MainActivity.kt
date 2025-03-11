package com.example.whattocook

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.whattocook.Fragments.FavouritesFragment
import com.example.whattocook.Fragments.HomeFragment
import com.example.whattocook.Fragments.SavedFragment
import com.example.whattocook.Fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

   lateinit var frameLayout : FrameLayout
    lateinit var bottomNav: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        frameLayout = findViewById(R.id.frameLayout)
        bottomNav =findViewById(R.id.btm_nav)

        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_btn -> {

                    switchFragments( HomeFragment())
                    true
                }
                R.id.saved_btn -> {
                    switchFragments( SavedFragment())
                    true
                }
                R.id.favourite_btn -> {
                    switchFragments( FavouritesFragment())
                    true
                }
                R.id.settings_btn -> {
                    switchFragments( SettingsFragment())
                    true
                }

                else -> false
            }
        }
        get_permission()
        switchFragments( HomeFragment())

    }
    private fun switchFragments(fragment : Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }
    fun get_permission(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA),100)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                get_permission()
            }
    }
}
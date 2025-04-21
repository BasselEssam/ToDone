package com.example.todone

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.todone.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.bottomNavBar.selectedItemId=R.id.home // to open home at first
        replaceFragment(HomeFragment())
        binding.bottomNavBar.setOnItemSelectedListener {
            if(it.itemId == R.id.home)
                replaceFragment(HomeFragment())
            else if(it.itemId == R.id.settings)
                replaceFragment(SettingsFragment())
            else if(it.itemId == R.id.add)
                replaceFragment(AddFragment())
            else replaceFragment(DoneFragment())
            true
        }

    }

    fun replaceFragment(fragment:Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }

}
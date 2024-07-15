package com.example.datingapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.datingapp.R
import com.example.datingapp.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener

class MainActivity : AppCompatActivity() ,OnNavigationItemSelectedListener{

    private lateinit var binding:ActivityMainBinding
    var actionBarDrawerToggle:ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpNavGraphWithBottomNav()
        setUpDrawerNavigation()


    }

    private fun setUpDrawerNavigation() {
        actionBarDrawerToggle = ActionBarDrawerToggle(this,binding.drawerLayout,
            R.string.open,
            R.string.close
        )
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle!!)
        actionBarDrawerToggle!!.syncState()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        binding.navigationView.setNavigationItemSelectedListener (this)
    }

    private fun setUpNavGraphWithBottomNav() {
        val navController = findNavController(R.id.hostFragment)
        NavigationUI.setupWithNavController(binding.bottomNav, navController)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            R.id.shareApp -> Toast.makeText(this,"ShareApp",Toast.LENGTH_SHORT).show()
            R.id.favourite -> Toast.makeText(this,"Favourite",Toast.LENGTH_SHORT).show()
            R.id.rateUs -> Toast.makeText(this,"Rate Us",Toast.LENGTH_SHORT).show()
            R.id.dev -> Toast.makeText(this,"Developer",Toast.LENGTH_SHORT).show()
            R.id.privacy -> Toast.makeText(this,"Privacy",Toast.LENGTH_SHORT).show()
            R.id.terms -> Toast.makeText(this,"Terms and Conditions",Toast.LENGTH_SHORT).show()

        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {    //for drawer icon ===
        return if(actionBarDrawerToggle!!.onOptionsItemSelected(item))
            true
        else
            super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START))
            binding.drawerLayout.close()
        else
            super.onBackPressed()
    }

}
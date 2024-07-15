package com.example.datingapp.ui.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import com.example.datingapp.R
import com.example.datingapp.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        hidingActionAndStatusBar()

        val currentUser = FirebaseAuth.getInstance().currentUser
        Handler(Looper.getMainLooper()).postDelayed({
            if(currentUser == null)
                startActivity(Intent(this,LoginActivity::class.java))
            else
                startActivity(Intent(this, MainActivity::class.java))
            finish()
        },2000)
    }



    private fun hidingActionAndStatusBar() {
        supportActionBar?.hide()  //action  bar
        if (Build. VERSION.SDK_INT >= Build. VERSION_CODES.R) {
            val decorView = this.window.decorView
            decorView.windowInsetsController?.hide(WindowInsets. Type.statusBars())
        } //add one plugin in app module , id 'kotlin-android-extensions' ans write this code
    }

}
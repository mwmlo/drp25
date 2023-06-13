package com.example.drp25

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.e("splashscreen", "opens splash screen")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Handler().postDelayed(Runnable { //This method will be executed once the timer is over
            // Start your app main activity
            startActivity(Intent(this@SplashScreenActivity, ChatActivity::class.java))
            // close this activity
            finish()
        }, 3000)
    }
}
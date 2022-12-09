package com.example.liveeasymobile.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.view.View
import com.example.liveeasymobile.databinding.ActivitySplashScreenBinding

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(
            layoutInflater
        )
        setContentView(binding.root)
        val decorView = window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        Handler().postDelayed({
            startActivity(
                Intent(
                    this@SplashScreenActivity,
                    MainActivity::class.java
                )
            )
            finish()
        }, 3000)
    }
}
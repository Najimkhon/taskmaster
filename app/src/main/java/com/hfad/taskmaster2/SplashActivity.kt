package com.hfad.taskmaster2

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager.LayoutParams.*
import com.hfad.taskmaster2.databinding.ActivitySplashBinding

private lateinit var binding: ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        window.setFlags(
            FLAG_FULLSCREEN,
            FLAG_FULLSCREEN
        )
        val typFace: Typeface = Typeface.createFromAsset(assets, "carbon bl.ttf")
        binding.tvAppName.typeface = typFace

    }
}
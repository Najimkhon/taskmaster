package com.hfad.taskmaster2.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager.LayoutParams.*
import com.google.firebase.ktx.Firebase
import com.hfad.taskmaster2.activities.IntroActivity
import com.hfad.taskmaster2.databinding.ActivitySplashBinding
import com.hfad.taskmaster2.firebase.FirestoreClass


class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
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

        Handler().postDelayed({
            var currentUserId = FirestoreClass().getCurrentUserId()
            if(currentUserId.isNotEmpty()){
                startActivity(Intent(this, MainActivity::class.java))
            }else{
            startActivity(Intent(this, IntroActivity::class.java))}

            finish()
        }, 2500)

    }
}
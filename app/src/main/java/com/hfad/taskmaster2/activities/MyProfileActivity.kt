package com.hfad.taskmaster2.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hfad.taskmaster2.R
import com.hfad.taskmaster2.databinding.ActivityMainBinding
import com.hfad.taskmaster2.databinding.ActivityMyProfileBinding

class MyProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityMyProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpActionBar()
    }

    private fun setUpActionBar(){

        setSupportActionBar(binding.toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)

        }
        binding.toolbarMyProfileActivity.setNavigationOnClickListener{ onBackPressed()}

    }
}
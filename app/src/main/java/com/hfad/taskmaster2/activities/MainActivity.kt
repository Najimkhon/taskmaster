package com.hfad.taskmaster2.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.hfad.taskmaster2.R
import com.hfad.taskmaster2.databinding.ActivityMainBinding
import com.hfad.taskmaster2.databinding.NavHeaderMainBinding
import com.hfad.taskmaster2.firebase.FirestoreClass
import com.hfad.taskmaster2.models.User
import com.hfad.taskmaster2.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mUserName: String
    companion object{
        const val MY_PROFILE_REQUEST_CODE:Int = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpActionBar()
        binding.navView.setNavigationItemSelectedListener(this)
        FirestoreClass().loadUserData(this)
        binding.mainAppBarLayout.fabCreateBoard.setOnClickListener{
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivity(intent)
        }
    }

    fun updateNavigationUserDetails(user: User){
        mUserName = user.name
        val headerView = binding.navView.getHeaderView(0)
        val headerBinding = NavHeaderMainBinding.bind(headerView)


        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(headerBinding.ivUserImage)
        headerBinding.tvUsername.text = user.name

    }

    private fun setUpActionBar(){

        setSupportActionBar(binding.mainAppBarLayout.toolbarMainActivity)
        binding.mainAppBarLayout.toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        binding.mainAppBarLayout.toolbarMainActivity.setNavigationOnClickListener{
            toggleDrawer()
        }
    }


    private fun toggleDrawer(){

        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile ->{
                startActivityForResult(Intent(this, MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK )
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE){
            FirestoreClass().loadUserData(this)
        }else{
            Log.e("Cancelled", "Requestcode: $requestCode" +
                    "resultCode: $resultCode" +
                    "activity code: ${Activity.RESULT_OK}")
        }
    }
}
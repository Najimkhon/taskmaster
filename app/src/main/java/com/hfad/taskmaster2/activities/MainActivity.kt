package com.hfad.taskmaster2.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.hfad.taskmaster2.R
import com.hfad.taskmaster2.databinding.ActivityMainBinding
import com.hfad.taskmaster2.databinding.ActivitySignInBinding
import com.hfad.taskmaster2.databinding.AppBarMainBinding

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpActionBar()
        binding.navView.setNavigationItemSelectedListener(this)
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
                Toast.makeText(this, "My Profile", Toast.LENGTH_SHORT).show()
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
}
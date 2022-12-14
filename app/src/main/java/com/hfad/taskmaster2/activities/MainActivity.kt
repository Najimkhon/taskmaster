package com.hfad.taskmaster2.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.JsonToken
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.installations.FirebaseInstallations
import com.hfad.taskmaster2.R
import com.hfad.taskmaster2.adapters.BoardItemsAdapter
import com.hfad.taskmaster2.databinding.ActivityMainBinding
import com.hfad.taskmaster2.databinding.NavHeaderMainBinding
import com.hfad.taskmaster2.firebase.FirestoreClass
import com.hfad.taskmaster2.models.Board
import com.hfad.taskmaster2.models.Task
import com.hfad.taskmaster2.models.User
import com.hfad.taskmaster2.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mUserName: String
    private lateinit var mSharedPreferences: SharedPreferences

    companion object{
        const val MY_PROFILE_REQUEST_CODE:Int = 11
        const val CREATE_BOARD_REQUEST_CODE:Int = 12
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpActionBar()
        binding.navView.setNavigationItemSelectedListener(this)

        mSharedPreferences = this.getSharedPreferences(Constants.TASKMASTER_PREFERENCES, Context.MODE_PRIVATE)

        val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)

        if(tokenUpdated){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().loadUserData(this, true)
        }else{
            FirebaseInstallations.getInstance().getToken(true)
                .addOnSuccessListener(this@MainActivity){
                        result ->
                    updateFCMToken(result.token)
                }
        }


        FirestoreClass().loadUserData(this, true)
        binding.mainAppBarLayout.fabCreateBoard.setOnClickListener{
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }




    }

    fun tokenUpdateSuccess(){
        hideProgressDialog()
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().loadUserData(this, true)
    }


    fun updateNavigationUserDetails(user: User, readBoardList: Boolean){
        hideProgressDialog()
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

        if (readBoardList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }

    }

    fun populateBoardsListUI(boardsList: ArrayList<Board>){
        hideProgressDialog()
        if (boardsList.size>0){
            binding.mainAppBarLayout.mainContentLayout.rvBoardsList.visibility = View.VISIBLE
            binding.mainAppBarLayout.mainContentLayout.tvNoBoardsAvailable.visibility = View.GONE

            binding.mainAppBarLayout.mainContentLayout.rvBoardsList.layoutManager = LinearLayoutManager(this)
            binding.mainAppBarLayout.mainContentLayout.rvBoardsList.setHasFixedSize(true)
            val adapter = BoardItemsAdapter(this, boardsList)
            binding.mainAppBarLayout.mainContentLayout.rvBoardsList.adapter = adapter

            adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })
        }else{
            binding.mainAppBarLayout.mainContentLayout.rvBoardsList.visibility = View.GONE
            binding.mainAppBarLayout.mainContentLayout.tvNoBoardsAvailable.visibility = View.VISIBLE
        }
    }

    private fun updateFCMToken(token: String){
        val userHashMap = HashMap<String, Any>()
        userHashMap[Constants.FCM_TOKEN] = token
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().updateUserProfileData(this, userHashMap)
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

                mSharedPreferences.edit().clear().apply()

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
        }else if (resultCode==Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE){
            FirestoreClass().getBoardsList(this)
        }else
        {
            Log.e("Cancelled", "Requestcode: $requestCode" +
                    "resultCode: $resultCode" +
                    "activity code: ${Activity.RESULT_OK}")
        }
    }
}
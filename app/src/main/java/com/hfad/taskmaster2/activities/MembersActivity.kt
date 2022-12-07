package com.hfad.taskmaster2.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hfad.taskmaster2.R
import com.hfad.taskmaster2.adapters.MemberListItemsAdapter
import com.hfad.taskmaster2.databinding.ActivityMembersBinding
import com.hfad.taskmaster2.databinding.ActivityTaskListBinding
import com.hfad.taskmaster2.firebase.FirestoreClass
import com.hfad.taskmaster2.models.Board
import com.hfad.taskmaster2.models.User
import com.hfad.taskmaster2.utils.Constants

class MembersActivity : BaseActivity() {
    private lateinit var binding: ActivityMembersBinding
    private lateinit var mBoardDetails: Board
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpActionBar()
        if (intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
            val listOfMembers = mBoardDetails.assignedTo
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getAssignedMembersList(listOfMembers, this)
        }




    }

    private fun setUpActionBar(){

        setSupportActionBar(binding.toolbarMembersActivity)
        val actionBar = supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.members)

        }
        binding.toolbarMembersActivity.setNavigationOnClickListener{ onBackPressed()}

    }

    fun setUpUsersList(users: ArrayList<User>){
        binding.rvMembersList.layoutManager = LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)
        val adapter = MemberListItemsAdapter(this, users)
        binding.rvMembersList.adapter = adapter

    }
}


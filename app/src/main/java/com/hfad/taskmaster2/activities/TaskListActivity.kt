package com.hfad.taskmaster2.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hfad.taskmaster2.R
import com.hfad.taskmaster2.databinding.ActivityMyProfileBinding
import com.hfad.taskmaster2.databinding.ActivityTaskListBinding
import com.hfad.taskmaster2.firebase.FirestoreClass
import com.hfad.taskmaster2.models.Board
import com.hfad.taskmaster2.utils.Constants

class TaskListActivity : BaseActivity() {
    private lateinit var binding: ActivityTaskListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        var boardDocumentId = ""
        if (intent.hasExtra(Constants.DOCUMENT_ID)){
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardsDetails(this, boardDocumentId)
    }

    fun boardDetails(board: Board){
        hideProgressDialog()
        setUpActionBar(board.name)

    }

    private fun setUpActionBar(title: String){

        setSupportActionBar(binding.toolbarTaskListActivity)
        val actionBar = supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = title

        }
        binding.toolbarTaskListActivity.setNavigationOnClickListener{ onBackPressed()}

    }

}
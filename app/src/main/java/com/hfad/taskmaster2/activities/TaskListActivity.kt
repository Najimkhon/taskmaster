package com.hfad.taskmaster2.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.hfad.taskmaster2.R
import com.hfad.taskmaster2.adapters.TaskListItemsAdapter
import com.hfad.taskmaster2.databinding.ActivityMyProfileBinding
import com.hfad.taskmaster2.databinding.ActivityTaskListBinding
import com.hfad.taskmaster2.firebase.FirestoreClass
import com.hfad.taskmaster2.models.Board
import com.hfad.taskmaster2.models.Card
import com.hfad.taskmaster2.models.Task
import com.hfad.taskmaster2.utils.Constants

class TaskListActivity : BaseActivity() {
    private lateinit var binding: ActivityTaskListBinding
    private lateinit var mBoardDetails: Board
    private lateinit var mBoardDocumentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)



        if (intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardsDetails(this, mBoardDocumentId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members -> {
                var intent = Intent(this, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL, mBoardDetails)
                startActivityForResult(intent, MEMBERS_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == MEMBERS_REQUEST_CODE){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsDetails(this, mBoardDocumentId)
        }else{
            Log.e("cancelled", "cancelled")
        }
    }

    fun boardDetails(board: Board){
        mBoardDetails = board
        hideProgressDialog()
        setUpActionBar()
        val addTaskList = Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)

        binding.rvTaskList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTaskList.setHasFixedSize(true)
        val adapter = TaskListItemsAdapter(this, board.taskList)
        binding.rvTaskList.adapter = adapter

    }

    private fun setUpActionBar(){

        setSupportActionBar(binding.toolbarTaskListActivity)
        val actionBar = supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.name

        }
        binding.toolbarTaskListActivity.setNavigationOnClickListener{ onBackPressed()}

    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardsDetails(this, mBoardDetails.documentId)
    }

    fun createTaskList(taskListName: String){
        val task = Task(taskListName, FirestoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0, task)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun updateTaskList(position: Int, listName: String, model: Task){
        val task = Task(listName, model.createdBy)
        mBoardDetails.taskList[position] = task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun deleteTaskList(position: Int){
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size - 1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun createCard(position: Int, cardName: String){
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size -1)
        val cardAssignedUsersList: ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(FirestoreClass().getCurrentUserId())
        val card = Card(cardName, FirestoreClass().getCurrentUserId(), cardAssignedUsersList)

        val cardList = mBoardDetails.taskList[position].cardList
        cardList.add(card)

        val task = Task(
            mBoardDetails.taskList[position].title,
            mBoardDetails.taskList[position].createdBy,
            cardList
        )
        mBoardDetails.taskList[position] = task
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    companion object{
        const val MEMBERS_REQUEST_CODE: Int = 13
    }



}
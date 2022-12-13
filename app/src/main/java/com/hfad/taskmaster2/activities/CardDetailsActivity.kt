package com.hfad.taskmaster2.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.hfad.taskmaster2.R
import com.hfad.taskmaster2.adapters.CardMemberListItemsAdapter
import com.hfad.taskmaster2.databinding.ActivityCardDetailsBinding
import com.hfad.taskmaster2.databinding.ActivityTaskListBinding
import com.hfad.taskmaster2.dialogs.LabelColorListDialog
import com.hfad.taskmaster2.firebase.FirestoreClass
import com.hfad.taskmaster2.models.*
import com.hfad.taskmaster2.utils.Constants
import com.projemanag.dialogs.MembersListDialog

class CardDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityCardDetailsBinding
    private lateinit var mBoardDetails: Board
    private lateinit var mMembersDetailList: ArrayList<User>
    private var cardListPosition: Int = -1
    private var taskListPosition: Int = -1
    private var mSelectedColor: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        getIntentData()
        setUpActionBar()

        mSelectedColor = mBoardDetails.taskList[taskListPosition].cardList[cardListPosition].labelColor
        if (mSelectedColor.isNotEmpty()){
            setColor()
        }
        binding.etNameCardDetails.setText(mBoardDetails.taskList[taskListPosition].cardList[cardListPosition].name)
        binding.etNameCardDetails.setSelection(binding.etNameCardDetails.text.toString().length)
        binding.btnUpdateCardDetails.setOnClickListener{
            if (binding.etNameCardDetails.toString().isNotEmpty()){
                updateCardDetails()
            }
        }

        binding.tvSelectLabelColor.setOnClickListener{
            labelColorsListDialog()
        }

        binding.tvSelectMembers.setOnClickListener{
            membersListDialog()
        }

        setupSelectedMembersList()



    }

    private fun colorsList(): ArrayList<String>{
        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")
        return colorsList

    }

    private fun setColor(){
        binding.tvSelectLabelColor.text = ""
        binding.tvSelectLabelColor.setBackgroundColor(Color.parseColor(mSelectedColor))
    }


    private fun setUpActionBar(){

        setSupportActionBar(binding.toolbarCardDetailsActivity)
        val actionBar = supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.taskList[taskListPosition].cardList[cardListPosition].name

        }
        binding.toolbarCardDetailsActivity.setNavigationOnClickListener{ onBackPressed()}

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_delete_card -> {
                alertDialogForDeleteCard(mBoardDetails.taskList[taskListPosition].cardList[cardListPosition].name)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun membersListDialog(){
        var cardAssignedMembersList = mBoardDetails.taskList[taskListPosition].cardList[cardListPosition].assignedTo
        if (cardAssignedMembersList.size > 0){
            for (i in mMembersDetailList.indices){
                for (j in cardAssignedMembersList){
                    if (mMembersDetailList[i].id == j){
                        mMembersDetailList[i].selected = true
                    }
                }
            }
        }else{
            for (i in mMembersDetailList.indices){
                        mMembersDetailList[i].selected = false
            }
        }

        val listDialog = object: MembersListDialog(
            this,
            mMembersDetailList,
            resources.getString(R.string.str_select_member)
        ){
            override fun onItemSelected(user: User, action: String) {
                if (action == Constants.SELECT){
                    if (!mBoardDetails.taskList[taskListPosition].cardList[cardListPosition].assignedTo.contains(user.id)){
                        mBoardDetails.taskList[taskListPosition].cardList[cardListPosition].assignedTo.add(user.id)
                    }
                }else{
                    mBoardDetails.taskList[taskListPosition].cardList[cardListPosition].assignedTo.remove(user.id)
                    for (i in mMembersDetailList.indices){
                        if(mMembersDetailList[i].id == user.id){
                            mMembersDetailList[i].selected = false
                        }
                    }
                }

                setupSelectedMembersList()
            }
        }
        listDialog.show()
    }



    private fun getIntentData(){
        if (intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!

        }
        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)){
            taskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)){
            cardListPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)){
            mMembersDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setupSelectedMembersList(){
        val cardAssignedMembersList = mBoardDetails.taskList[taskListPosition].cardList[cardListPosition].assignedTo
        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

            for (i in mMembersDetailList.indices){
                for (j in cardAssignedMembersList){
                    if (mMembersDetailList[i].id == j){
                        val selectedMember = SelectedMembers(
                            mMembersDetailList[i].id,
                            mMembersDetailList[i].image
                        )
                        selectedMembersList.add(selectedMember)
                    }
                }
            }

        if (selectedMembersList.size > 0){
            selectedMembersList.add(SelectedMembers("", ""))
            binding.tvSelectMembers.visibility = View.GONE
            binding.rvSelectedMembersList.visibility = View.VISIBLE
            binding.rvSelectedMembersList.layoutManager = GridLayoutManager(this, 6)
            val adapter = CardMemberListItemsAdapter(this, selectedMembersList)
            binding.rvSelectedMembersList.adapter = adapter
            adapter.setOnClickListener(
                object: CardMemberListItemsAdapter.OnClickListener{
                    override fun onClick() {
                        membersListDialog()
                    }

                }
            )
        }else{
            binding.tvSelectMembers.visibility = View.VISIBLE
            binding.rvSelectedMembersList.visibility = View.GONE
        }

    }

    private fun updateCardDetails(){
        val card = Card(
            binding.etNameCardDetails.text.toString(),
            mBoardDetails.taskList[taskListPosition].cardList[cardListPosition].createdBy,
            mBoardDetails.taskList[taskListPosition].cardList[cardListPosition].assignedTo,
            mSelectedColor
        )

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)
        mBoardDetails.taskList[taskListPosition].cardList[cardListPosition] = card

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    private fun deleteCard(){
        var cardList: ArrayList<Card> = mBoardDetails.taskList[taskListPosition].cardList
        cardList.removeAt(cardListPosition)
        var taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)
        taskList[taskListPosition].cardList = cardList

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this, mBoardDetails)
    }

    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $cardName.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
            deleteCard()
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    private fun labelColorsListDialog(){
        val colorsList: ArrayList<String> = colorsList()
        val listDialog = object: LabelColorListDialog(this, colorsList, resources.getString(R.string.str_select_label_color), mSelectedColor){
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }

        }
        listDialog.show()
    }
}
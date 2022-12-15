package com.hfad.taskmaster2.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hfad.taskmaster2.R
import com.hfad.taskmaster2.adapters.MemberListItemsAdapter
import com.hfad.taskmaster2.databinding.ActivityMembersBinding
import com.hfad.taskmaster2.databinding.ActivityTaskListBinding
import com.hfad.taskmaster2.databinding.DialogSearchMemberBinding
import com.hfad.taskmaster2.firebase.FirestoreClass
import com.hfad.taskmaster2.models.Board
import com.hfad.taskmaster2.models.User
import com.hfad.taskmaster2.utils.Constants
import org.json.JSONObject
import java.io.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.URL

class MembersActivity : BaseActivity() {
    private lateinit var binding: ActivityMembersBinding
    private lateinit var mBoardDetails: Board
    private lateinit var mAssignedMembersList: ArrayList<User>
    private var anyChangesMade: Boolean = false

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
        hideProgressDialog()
        mAssignedMembersList = users
        binding.rvMembersList.layoutManager = LinearLayoutManager(this)
        binding.rvMembersList.setHasFixedSize(true)
        val adapter = MemberListItemsAdapter(this, users)
        binding.rvMembersList.adapter = adapter

    }

    fun memberDetails(user:User){
        mBoardDetails.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this, mBoardDetails, user)
    }

    fun memberAssignSuccess(user: User){
        hideProgressDialog()
        mAssignedMembersList.add(user)
        setUpUsersList(mAssignedMembersList)
        SendNotificationToUserAsyncTask(mBoardDetails.name, user.fcmToken).execute()
        anyChangesMade = true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member -> {
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun dialogSearchMember() {
        val binding = DialogSearchMemberBinding.inflate(LayoutInflater.from(this))
        val dialog = Dialog(this)
        dialog.setContentView(binding.root)
        binding.tvAdd.setOnClickListener{
            val email = binding.etEmailSearchMember.text.toString()
            if (email.isNotEmpty()){
                dialog.dismiss()
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this, email)
            }else{
                Toast.makeText(this, "Please, enter an email adress.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.tvCancel.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()

    }

    override fun onBackPressed() {
        if(anyChangesMade){
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }

    private inner class SendNotificationToUserAsyncTask(val boardName: String, val token: String): AsyncTask<Any, Void, String>() {

        override fun onPreExecute() {
            showProgressDialog(resources.getString(R.string.please_wait))
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: Any?): String {
            var result:String
            var connection: HttpURLConnection? = null
            try{
                val url = URL(Constants.FCM_BASE_URL)
                connection = url.openConnection() as HttpURLConnection
                connection.doOutput = true
                connection.doInput = true
                connection.instanceFollowRedirects = false
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("charset", "utf-8")
                connection.setRequestProperty("Accept", "application/json")

                connection.setRequestProperty(
                    Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}"
                )
                connection.useCaches = false
                val wr = DataOutputStream(connection.outputStream)
                val jsonRequest = JSONObject()
                val dataObject = JSONObject()
                dataObject.put(Constants.FCM_KEY_TITLE, "Assigned to the board $boardName")
                dataObject.put(Constants.FCM_KEY_MESSAGE, "You have been assigned to the Board by ${mAssignedMembersList[0].name}")

                jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
                jsonRequest.put(Constants.FCM_KEY_TO, token)

                wr.writeBytes(jsonRequest.toString())
                wr.flush()
                wr.close()

                val httpResult: Int = connection.responseCode
                if(httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val sb = StringBuilder()
                    var line: String?
                    try {
                        while (reader.readLine().also { line = it } != null){
                            sb.append(line+"\n")
                        }
                    }catch (e: IOException){
                        e.printStackTrace()
                    }finally {
                        try {
                            inputStream.close()
                        }catch (e:IOException){
                            e.printStackTrace()
                        }
                    }

                    result = sb.toString()

                }else{
                    result = connection.responseMessage
                }
            }catch (e: SocketTimeoutException){
                result = "Connection Timout"
            }catch (e:Exception){
                result = "Error: " + e.message
            }finally {
                connection?.disconnect()
            }

            return result
        }

        override fun onPostExecute(result: String?) {
            hideProgressDialog()
            super.onPostExecute(result)
        }

    }
}


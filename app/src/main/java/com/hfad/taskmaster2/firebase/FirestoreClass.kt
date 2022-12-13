package com.hfad.taskmaster2.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.hfad.taskmaster2.activities.*
import com.hfad.taskmaster2.models.Board
import com.hfad.taskmaster2.models.Card
import com.hfad.taskmaster2.models.User
import com.hfad.taskmaster2.utils.Constants
import kotlinx.coroutines.CoroutineScope


class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()
    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "error", e)
            }

    }

    fun createBoard(activity: CreateBoardActivity, board: Board) {
        mFireStore.collection(Constants.BOARDS)
            .document().set(board, SetOptions.merge()).addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Board created successfully")
                Toast.makeText(activity, "Board created successfully", Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "error", e)
            }
    }

    fun getBoardsList(activity: MainActivity) {
        mFireStore.collection(Constants.BOARDS)
            // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                // Here we have created a new instance for Boards ArrayList.
                val boardsList: ArrayList<Board> = ArrayList()

                // A for loop as per the list of documents to convert them into Boards ArrayList.
                for (i in document.documents) {

                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id

                    boardsList.add(board)
                }

                // Here pass the result to the base activity.
                activity.populateBoardsListUI(boardsList)
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "profile data updated")
                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
                Toast.makeText(activity, "error on updating profile", Toast.LENGTH_SHORT).show()
            }


    }

    fun loadUserData(activity: Activity, readBoardsList: Boolean = false) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).get().addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!

                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUi(loggedInUser)
                    }
                }


            }.addOnFailureListener { e ->
                Log.e("SignInUser", "error")
            }


    }


    fun getCurrentUserId(): String {

        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser != null) {
            currentUserId = currentUser.uid
        }
        return currentUserId

    }

    fun getAssignedMembersList(assignedTo: ArrayList<String>, activity: Activity) {
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID, assignedTo)
            .get()
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, it.documents.toString())
                val usersList = ArrayList<User>()
                it.forEach {
                    usersList.add(it.toObject(User::class.java))
                }

                if (activity is MembersActivity)
                    activity.setUpUsersList(usersList)
                else if (activity is TaskListActivity)
                    activity.boardUsersList(usersList)
            }.addOnFailureListener {
                if (activity is MembersActivity){
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while getting users list", it)}
                else if(activity is TaskListActivity){
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while getting users list", it)
                }
            }
    }

    fun getBoardsDetails(activity: TaskListActivity, boardDocumentId: String) {
        mFireStore.collection(Constants.BOARDS)
            // A where array query as we want the list of the board in which the user is assigned. So here you can pass the current user id.
            .document(boardDocumentId)
            .get() // Will get the documents snapshots.
            .addOnSuccessListener { document ->
                // Here we get the list of boards in the form of documents.
                val board = document.toObject(Board::class.java)!!
                board.documentId = document.id
                Log.i(activity.javaClass.simpleName, document.toString())
                activity.boardDetails(board)
            }
            .addOnFailureListener { e ->

                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }

    fun addUpdateTaskList(activity: Activity, board: Board) {
        val taskListHashMap = HashMap<String, Any>()
        taskListHashMap[Constants.TASK_LIST] = board.taskList
        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(taskListHashMap)
            .addOnSuccessListener {
                if (activity is TaskListActivity) {
                    Log.i(activity.javaClass.simpleName, "Tasklist is updated!")
                    activity.addUpdateTaskListSuccess()
                } else
                    if (activity is CardDetailsActivity) {
                        activity.addUpdateTaskListSuccess()
                    }
            }.addOnFailureListener { e ->
                if (activity is TaskListActivity) {
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName, "Error while updating a board.", e)
                } else
                    if (activity is CardDetailsActivity) {
                        activity.hideProgressDialog()
                        Log.e(activity.javaClass.simpleName, "Error while updating a board.", e)
                    }
            }
    }

    fun getMemberDetails(activity: MembersActivity, email: String) {
        mFireStore.collection(Constants.USERS)
            .whereEqualTo(Constants.EMAIL, email)
            .get()
            .addOnSuccessListener {
                if (it.documents.size > 0) {
                    val user = it.documents[0].toObject(User::class.java)!!
                    activity.memberDetails(user)
                } else {
                    activity.hideProgressDialog()
                    activity.showSnackBar("No such member found!")
                }
            }.addOnFailureListener {
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting user details!", it
                )


            }
    }

    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User) {
        val assignedToHashMap = HashMap<String, Any>()
        assignedToHashMap[Constants.ASSIGNED_TO] = board.assignedTo
        mFireStore.collection(Constants.BOARDS)
            .document(board.documentId)
            .update(assignedToHashMap)
            .addOnSuccessListener {
                activity.memberAssignSuccess(user)
            }.addOnFailureListener {
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while assigning user to the board!", it
                )
            }

    }

}
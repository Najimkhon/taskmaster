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
import com.hfad.taskmaster2.models.User
import com.hfad.taskmaster2.utils.Constants


class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()
    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge()).addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener{
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "error", e)
            }

    }

    fun createBoard(activity: CreateBoardActivity, board: Board){
        mFireStore.collection(Constants.BOARDS)
            .document(getCurrentUserId()).set(board, SetOptions.merge()).addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Board created successfully")
                Toast.makeText(activity, "Board created successfully", Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener{
                    e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "error", e)
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap:HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "profile data updated")
                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener{
                e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a board.",
                    e
                )
                Toast.makeText(activity, "error on updating profile", Toast.LENGTH_SHORT).show()
            }


    }

    fun loadUserData(activity: Activity){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).get().addOnSuccessListener { document->
                val loggedInUser = document.toObject(User::class.java)!!

                when (activity){
                    is SignInActivity->{
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity->{
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is MyProfileActivity->{
                        activity.setUserDataInUi(loggedInUser)
                    }
                }


            }.addOnFailureListener{
                    e->
                Log.e("SignInUser", "error")
            }


    }


     fun getCurrentUserId():String{

        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""
        if (currentUser!=null){
            currentUserId = currentUser.uid
        }
        return currentUserId

    }

}
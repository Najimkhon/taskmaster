package com.hfad.taskmaster2.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.hfad.taskmaster2.activities.BaseActivity
import com.hfad.taskmaster2.activities.SignUpActivity
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
                Log.e(activity.javaClass.simpleName, "error")
            }

    }

    private fun getCurrentUserId():String{
        return FirebaseAuth.getInstance().currentUser!!.uid

    }

}
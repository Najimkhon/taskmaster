package com.hfad.taskmaster2.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.taskmaster2.activities.BaseActivity
import com.hfad.taskmaster2.activities.SignUpActivity
import com.hfad.taskmaster2.models.User


class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()
    fun registerUser(activity: SignUpActivity, userInfo: User){

    }

}
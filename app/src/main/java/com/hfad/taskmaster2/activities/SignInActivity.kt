package com.hfad.taskmaster2.activities

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.hfad.taskmaster2.R
import com.hfad.taskmaster2.databinding.ActivitySignInBinding

class SignInActivity : BaseActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        val view = binding.root
        setContentView(view)
        setUpActionBar()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding.btnSignIn.setOnClickListener{
            signInRegisteredUser()
        }
    }

    private fun signInRegisteredUser(){
        val email: String = binding.etEmailSignIn.text.toString().trim{it<=' '}
        val password: String = binding.etPasswordSignIn.text.toString().trim{it<=' '}

        if (validateForm(email, password)){
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    hideProgressDialog()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }

        }

    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarSignInActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding.toolbarSignInActivity.setNavigationOnClickListener{onBackPressed()}


    }
    private fun validateForm(email:String, password:String):Boolean{
        var result:Boolean
        return when {
            TextUtils.isEmpty(email)->{
                showSnackBar("Please enter a email")
                false
            }
            TextUtils.isEmpty(password)->{
                showSnackBar("Please enter a password")
                false
            }else -> {
                true
            }
        }
    }
}
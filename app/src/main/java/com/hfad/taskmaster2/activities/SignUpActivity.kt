package com.hfad.taskmaster2.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hfad.taskmaster2.R
import com.hfad.taskmaster2.databinding.ActivitySignUpBinding
import com.hfad.taskmaster2.firebase.FirestoreClass
import com.hfad.taskmaster2.models.User

class SignUpActivity : BaseActivity() {
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpActionBar()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    fun userRegisteredSuccess(){
        Toast.makeText(this, "You have successfully registered", Toast.LENGTH_LONG).show()
        hideProgressDialog()
        FirebaseAuth.getInstance().signOut()
        finish()

    }

    private fun setUpActionBar(){
        setSupportActionBar(binding.toolbarSignUpActivity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }
        binding.toolbarSignUpActivity.setNavigationOnClickListener{onBackPressed()}
        binding.btnSignUp.setOnClickListener{registerUser()}
    }

    private fun registerUser(){
        val name: String = binding.etName.text.toString().trim{it<=' '}
        val email: String = binding.etEmail.text.toString().trim{it<=' '}
        val password: String = binding.etPassword.text.toString().trim{it<=' '}
        if (validateForm(name, email, password)){
           showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password).addOnCompleteListener{
                    if(it.isSuccessful){
                        val firebaseUser: FirebaseUser = it.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val user = User(firebaseUser.uid, name, registeredEmail)
                        FirestoreClass().registerUser(this,user)
                    }else{
                        Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }

        }

    }

    private fun validateForm(name:String, email:String, password:String):Boolean{
        var result:Boolean
        return when {
            TextUtils.isEmpty(name)->{
                showSnackBar("Please enter a name")
                false
            }
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
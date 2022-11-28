package com.hfad.taskmaster2.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.hfad.taskmaster2.R
import com.hfad.taskmaster2.databinding.ActivityMainBinding
import com.hfad.taskmaster2.databinding.ActivitySignInBinding
import com.hfad.taskmaster2.databinding.DialogProgressBinding

open class BaseActivity : AppCompatActivity() {
    private lateinit var binding: DialogProgressBinding
    private var doubleBackPressedOnce = false
    private lateinit var mProgressDialog:Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogProgressBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }

    fun showProgressDialog(text:String){
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        binding.tvProgressText.text = text
        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun getCurrentUserID():String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun doubleBackToExit(){
        if (doubleBackPressedOnce){
            super.onBackPressed()
            return
        }

        this.doubleBackPressedOnce = true
        Toast.makeText(this, resources.getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show()

        Handler().postDelayed({doubleBackPressedOnce = false},2000)
    }

    fun showSnackBar(message: String){
        val snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackbar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snackbar_error_color))
        snackbar.show()
    }
}
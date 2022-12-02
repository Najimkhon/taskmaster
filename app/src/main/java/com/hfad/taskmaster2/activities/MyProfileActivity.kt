package com.hfad.taskmaster2.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.hfad.taskmaster2.R
import com.hfad.taskmaster2.databinding.ActivityMyProfileBinding
import com.hfad.taskmaster2.firebase.FirestoreClass
import com.hfad.taskmaster2.models.User
import com.hfad.taskmaster2.utils.Constants
import com.squareup.picasso.Picasso
import java.io.IOException

class MyProfileActivity : BaseActivity() {

    private lateinit var mUserDetails: User
    private var mSelectedImageUri: Uri? = null
    private var mProfileImageURL: String = ""

    private lateinit var binding: ActivityMyProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setUpActionBar()
        FirestoreClass().loadUserData(this)
        binding.ivUserImage.setOnClickListener{
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }

        binding.btnUpdate.setOnClickListener{
            if (mSelectedImageUri != null){
                uploadImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                Toast.makeText(this, "You denied the permission to storage. You can allow it from settings. ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE && data!!.data!! != null)
        {
            mSelectedImageUri = data.data

            try {
                Glide
                    .with(this)
                    .load(mSelectedImageUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(binding.ivUserImage)
            }catch (e: IOException){
                e.printStackTrace()

            }

        }
    }


    fun setUserDataInUi(user:User){

        mUserDetails = user

        try {
            Glide
                .with(this)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(binding.ivUserImage)
        }catch (e: IOException){
            e.printStackTrace()

        }


        binding.etName.setText(user.name)
        binding.etEmail.setText(user.email)
        if (user.mobile != 0L) {
            binding.etMobile.setText(user.mobile.toString())
        }

    }

    private fun updateUserProfileData(){
        val userHashMap = HashMap<String, Any>()
        var anyChangesMade = false
        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image){
            userHashMap[Constants.IMAGE] = mProfileImageURL
            anyChangesMade = true
        }
        if (binding.etName.text.toString() != mUserDetails.name){
            userHashMap[Constants.NAME] = binding.etName.text.toString()
            anyChangesMade = true
        }
        if (binding.etMobile.text.toString() != mUserDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = binding.etMobile.text.toString().toLong()
            anyChangesMade = true
        }
        if (anyChangesMade){ FirestoreClass().updateUserProfileData(this, userHashMap)}
    }



    private fun setUpActionBar(){

        setSupportActionBar(binding.toolbarMyProfileActivity)
        val actionBar = supportActionBar
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile)

        }
        binding.toolbarMyProfileActivity.setNavigationOnClickListener{ onBackPressed()}

    }

    private fun uploadImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mSelectedImageUri != null){
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child("USER_IMAGE" + System.currentTimeMillis() + "." + Constants.getFileExtension(this,mSelectedImageUri))
            sRef.putFile(mSelectedImageUri!!).addOnSuccessListener {
                taskSnapshot->
                Log.i("Firebase Image url", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri->
                    Log.i("Downloadable Image URI", uri.toString())
                    mProfileImageURL = uri.toString()
                    updateUserProfileData()
                }
            }.addOnFailureListener {
                exception->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                hideProgressDialog()
            }
        }
    }


    fun profileUpdateSuccess(){
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()

    }

}
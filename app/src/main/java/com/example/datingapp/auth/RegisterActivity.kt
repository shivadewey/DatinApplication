package com.example.datingapp.auth

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.datingapp.ui.activity.MainActivity
import com.example.datingapp.databinding.ActivityRegisterBinding
import com.example.datingapp.model.UserModel
import com.example.datingapp.utils.Config
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private var imageUri : Uri? = null
    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()){
        imageUri = it
        binding.ivUserImage.setImageURI(imageUri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            ivUserImage.setOnClickListener {
                selectImage.launch("image/*")
            }
            btnContinue.setOnClickListener {
                validateUser()
            }
        }


    }

    private fun validateUser() {
        val userName = binding.userName.text.toString()
        val userEmail = binding.userEmail.text.toString()
        val userCity = binding.userCity.text.toString()
        val checkBox = binding.checkBoxTermCondition.isChecked
        if(userName.isEmpty() || userEmail.isEmpty() || userCity.isEmpty() || imageUri == null) Toast.makeText(this,"Enter all fields please",Toast.LENGTH_SHORT).show()
        else if(!checkBox) Toast.makeText(this,"Please accept term and condition",Toast.LENGTH_SHORT).show()
        else uploadImage()
    }

    private fun uploadImage() {
        Config.showDialog(this)
        val currentUserUid  = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val storageReference=FirebaseStorage.getInstance().getReference("Profile")
            .child(currentUserUid)
            .child("Profile.jpg")
        storageReference.putFile(imageUri!!)
            .addOnSuccessListener {
                storageReference.downloadUrl
                    .addOnSuccessListener {
                    storeData(it)
                }
                    .addOnFailureListener{
                        Config.hideDialog()
                        Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener{
                Config.hideDialog()
                Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT).show()
            }

    }

    private fun storeData(imageUrl: Uri?) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->     //first generating the token and store it to the respective user database
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            // Get new FCM registration token
           val  token = task.result

            val userData = UserModel(
                name = binding.userName.text.toString(),
                email =  binding.userEmail.text.toString(),
                city = binding.userCity.text.toString(),
                image = imageUrl.toString(),
                number = FirebaseAuth.getInstance().currentUser?.phoneNumber,
                fcmToken =  token)

            FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
                .setValue(userData)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                        Toast.makeText(this,"Successfully registered",Toast.LENGTH_SHORT).show()
                    }
                    else
                        Toast.makeText(this,it.exception.toString(),Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT).show()
                }
        })


    }
}
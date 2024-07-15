package com.example.datingapp.auth

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.datingapp.ui.activity.MainActivity
import com.example.datingapp.R
import com.example.datingapp.databinding.ActivityLoginBinding
import com.example.datingapp.model.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var firebaseAuth = FirebaseAuth.getInstance()
    private var verificationId: String? = null
    private lateinit var progressDialog: AlertDialog
    private lateinit var tokenSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        progressBarNumber.visibility = GONE

        progressDialog =
            AlertDialog.Builder(this).setView(R.layout.progress_dialog).setCancelable(false)
                .create()

        binding.apply {
            btnSendOtp.setOnClickListener {
                if (binding.userNumber.text!!.isEmpty())
                    binding.userNumber.error = "Please Enter Your Number"
                else {
                    sendOTP(binding.userNumber.text.toString())
                }
            }

            btnVerifyOtp.setOnClickListener {
                if (binding.userOtp.text!!.isEmpty())
                    binding.userOtp.error = "Please Enter Your OTP"
                else {
                    verifyOTP(binding.userOtp.text.toString())
                }
            }

        }
    }

    private fun verifyOTP(userOtp: String) {
//        progressBarNumber.visibility = VISIBLE
        progressDialog.show()
        val credential = PhoneAuthProvider.getCredential(verificationId!!, userOtp)
        signInWithPhoneAuthCredential(credential)
    }


    private fun sendOTP(userNumber: String) {
//        progressBarNumber.visibility = VISIBLE
        progressDialog.show()
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                progressBarNumber.visibility = VISIBLE
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                this@LoginActivity.verificationId = verificationId
//                progressBarNumber.visibility = GONE
                progressDialog.dismiss()
                binding.cvNumberLayout.visibility = GONE
                binding.cvOtpLayout.visibility = VISIBLE
//                progressBarNumber.visibility = GONE
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber("+91$userNumber")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
//                progressBarNumber.visibility = VISIBLE
                if (task.isSuccessful) {
//                    progressBarNumber.visibility = VISIBLE
                    checkUser(binding.userNumber.text.toString())

                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUser(userNumber: String) {
        FirebaseDatabase.getInstance().getReference("Users").child("+91$userNumber")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userdata = snapshot.getValue(UserModel::class.java)
                        tokenSharedPreferences = getSharedPreferences("NewToken", MODE_PRIVATE)
                        val token = tokenSharedPreferences.getString("newToken", "")
                        if (userdata?.fcmToken != token) {
                            Log.d("afterif", userdata?.fcmToken!!)
                            FirebaseDatabase.getInstance().getReference("Users")
                                .child("+91$userNumber")
                                .child("fcmToken").setValue(token)
                        }
                        progressDialog.dismiss()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        progressDialog.dismiss()
                        startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
                        finish()
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    progressDialog.dismiss()
                    Toast.makeText(this@LoginActivity, error.message, Toast.LENGTH_SHORT).show()
                }

            })
    }


}
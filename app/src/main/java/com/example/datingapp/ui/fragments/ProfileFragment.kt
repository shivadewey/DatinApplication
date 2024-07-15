package com.example.datingapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.datingapp.R
import com.example.datingapp.auth.LoginActivity
import com.example.datingapp.databinding.FragmentProfileBinding
import com.example.datingapp.model.UserModel
import com.example.datingapp.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {

    private lateinit var binding:FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)

        Config.showDialog(requireContext())
        lifecycleScope.launch {
            settingUserProfile()
        }

        binding.apply {
            btnLogout.setOnClickListener {
                singOutTheUser()
            }
            btnEditProfile.setOnClickListener {
                editUserProfile()
            }
        }
        return binding.root
    }

    private fun editUserProfile() {

    }

    private fun singOutTheUser() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(requireContext(),LoginActivity::class.java))
        requireActivity().finish()
    }

    private suspend fun settingUserProfile() {
        delay(1000)
        val currentUserPhoneNumber = FirebaseAuth.getInstance().currentUser?.phoneNumber

        FirebaseDatabase.getInstance().getReference("Users").child(currentUserPhoneNumber!!).get()
            .addOnSuccessListener { userProfileData->
                if(userProfileData.exists()){

                    val userProfile = userProfileData.getValue(UserModel::class.java)
                    Glide.with(requireContext()).load(userProfile?.image).placeholder(R.drawable.ic_baseline_account_circle_24).into(binding.ivUserImage)
                    binding.apply {
                        userName.setText(userProfile?.name)
                        userCity.setText(userProfile?.city)
                        userEmail.setText(userProfile?.email)
                        userNumber.setText(userProfile?.number)
                    }
                    Config.hideDialog()
                }
            }
    }


}
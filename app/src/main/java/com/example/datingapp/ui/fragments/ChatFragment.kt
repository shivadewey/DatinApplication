package com.example.datingapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datingapp.adapter.ChatFragmentAdapter
import com.example.datingapp.databinding.FragmentChatragmentBinding
import com.example.datingapp.utils.Config
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ChatFragment : Fragment() {

    private lateinit var binding:FragmentChatragmentBinding
    private lateinit var chatFragmentAdapter: ChatFragmentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatragmentBinding.inflate(layoutInflater)

        prepareRvForChatFragmentAdapter()
        getUsersWithWhomChatted()


        return binding.root
    }

    private fun getUsersWithWhomChatted() {

        Config.showDialog(requireContext())

        val currentUserId = FirebaseAuth.getInstance().currentUser?.phoneNumber
        FirebaseDatabase.getInstance().getReference("Chats")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listOfReceiverNumber = ArrayList<String>()      //this should be inside the on data  otherwise every time , already added user will
                    val listOfChatsWithinTheCharId = ArrayList<String>() //be also in the list
                   for(charIds in snapshot.children){
                       if(charIds.key!!.contains(currentUserId!!)){
                           listOfReceiverNumber.add(charIds.key!!.replace(currentUserId,""))
                           listOfChatsWithinTheCharId.add(charIds.key!!)
                       }
                   }
                    chatFragmentAdapter.setUserList(listOfReceiverNumber)
                    chatFragmentAdapter.setChats(listOfChatsWithinTheCharId)
                    Config.hideDialog()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


    }


    private fun prepareRvForChatFragmentAdapter() {
        chatFragmentAdapter = ChatFragmentAdapter(requireContext())
        binding.userChatRv.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = chatFragmentAdapter
        }
    }


}
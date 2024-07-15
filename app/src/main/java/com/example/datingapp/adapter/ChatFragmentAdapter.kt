package com.example.datingapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.datingapp.databinding.ChatItemViewBinding
import com.example.datingapp.model.UserModel
import com.example.datingapp.ui.activity.MessageActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragmentAdapter(val context: Context):RecyclerView.Adapter<ChatFragmentAdapter.ChatViewHolder>() {

    private var listOfReceiverNumber = ArrayList<String>()
    private var listOfChatsWithinTheCharId = ArrayList<String>()

    fun setUserList(userList: ArrayList<String>){
        this.listOfReceiverNumber = userList
        notifyDataSetChanged()
    }

    fun setChats(listOfChatsWithinTheCharId : ArrayList<String>){
        this.listOfChatsWithinTheCharId = listOfChatsWithinTheCharId
        notifyDataSetChanged()
    }

    class ChatViewHolder(val binding:ChatItemViewBinding):ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(ChatItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }


    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

       val receiverNumber  = listOfReceiverNumber[position]
        FirebaseDatabase.getInstance().getReference("Users").child(receiverNumber)
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val receiverData = snapshot.getValue(UserModel::class.java)
                        holder.binding.apply {
                            Glide.with(holder.itemView).load(receiverData!!.image).into(userImage)
                            userName.text = receiverData.name
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                   Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
                }

            })

        holder.itemView.setOnClickListener {
            val intent = Intent(context,MessageActivity::class.java)
            intent.putExtra("chatId",listOfChatsWithinTheCharId[position])
            intent.putExtra("userId",listOfReceiverNumber[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
       return listOfReceiverNumber.size
    }


}
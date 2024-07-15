package com.example.datingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.datingapp.R
import com.example.datingapp.databinding.ReceiveMessageBinding
import com.example.datingapp.databinding.SendMessageBinding
import com.example.datingapp.model.Messages
import com.example.datingapp.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MessageAdapter(private val recyclerView: RecyclerView, val context:Context):RecyclerView.Adapter<ViewHolder>() {

    private var messageList = ArrayList<Messages>()
    var ITEM_SENT = 1
    var ITEM_RECEIVED =2

    fun setMessageList(messageList: ArrayList<Messages>){
        this.messageList = messageList
        notifyDataSetChanged()
        recyclerView.post{
            recyclerView.smoothScrollToPosition(itemCount - 1)
        }
    }

class SentViewHolder(val binding:SendMessageBinding):ViewHolder(binding.root)
class ReceivedViewHolder(val binding:ReceiveMessageBinding):ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if(viewType == 1)
            SentViewHolder(SendMessageBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        else
            ReceivedViewHolder(ReceiveMessageBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMessages = messageList[position]
        if(holder.javaClass == SentViewHolder::class.java){
            holder as SentViewHolder
            holder.binding.tvSendMessage.text = currentMessages.message

            FirebaseDatabase.getInstance().getReference("Users").child(currentMessages.senderId!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val receiverData = snapshot.getValue(UserModel::class.java)
                            holder.binding.apply {
                                Glide.with(holder.itemView).load(receiverData!!.image).placeholder(R.drawable.ic_baseline_account_circle_24).into(senderImage)
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context,error.message, Toast.LENGTH_SHORT).show()
                    }
                })
        }
        else{
            holder as ReceivedViewHolder
            holder.binding.tvReceiveMessage.text  = currentMessages.message
            FirebaseDatabase.getInstance().getReference("Users").child(currentMessages.senderId!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            val receiverData = snapshot.getValue(UserModel::class.java)
                            holder.binding.apply {
                                Glide.with(holder.itemView).load(receiverData!!.image).placeholder(R.drawable.ic_baseline_account_circle_24).into(receiverImage)
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context,error.message, Toast.LENGTH_SHORT).show()
                    }
                })

        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        val currentUserId = FirebaseAuth.getInstance().currentUser?.phoneNumber
        return  if(currentUserId == currentMessage.senderId) ITEM_SENT
        else
            ITEM_RECEIVED
    }

}
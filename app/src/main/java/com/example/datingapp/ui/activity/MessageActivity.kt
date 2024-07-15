package com.example.datingapp.ui.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.datingapp.adapter.MessageAdapter
import com.example.datingapp.databinding.ActivityMessageBinding
import com.example.datingapp.model.Messages
import com.example.datingapp.model.UserModel
import com.example.datingapp.notification.NotificationData
import com.example.datingapp.notification.PushNotification
import com.example.datingapp.notification.api.ApiUtilities
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMessageBinding
    private lateinit var messageAdapter: MessageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Message"

        prepareRvForMessageAdapter()

        verifyChatId()
//        getMessages(intent.getStringExtra("chatId"))

        binding.send.setOnClickListener{
            val message = binding.message.text.toString()
            binding.message.text.clear()
            if(message.isEmpty()) Toast.makeText(this,"Enter Message",Toast.LENGTH_SHORT).show()
            else
                storeMessage(message)
        }


    }

    private var senderId:String?= null
    private var chatId:String?= null
    private var receiverId: String? = null

    private fun verifyChatId() {
        receiverId = intent.getStringExtra("userId")
         senderId  = FirebaseAuth.getInstance().currentUser?.phoneNumber
         chatId = senderId + receiverId
        val reverseChatId =  receiverId + senderId
        val reference=FirebaseDatabase.getInstance().getReference("Chats")
        reference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.hasChild(chatId!!)){
                    getMessages(chatId)
                }
                else if(snapshot.hasChild(reverseChatId)){
                    chatId = reverseChatId
                    getMessages(chatId)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessageActivity,error.message,Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun prepareRvForMessageAdapter() {
        messageAdapter = MessageAdapter(binding.rvChats,this)
        binding.rvChats.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
            adapter  = messageAdapter
        }
    }



    private fun getMessages(chatId: String?) {
        FirebaseDatabase.getInstance().getReference("Chats").child(chatId!!)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messagesList = ArrayList<Messages>()
                    for(messages in snapshot.children){
                        val message = messages.getValue(Messages::class.java)
                        messagesList.add(message!!)
                    }
                    messageAdapter.setMessageList(messagesList)
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MessageActivity, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun storeMessage(message: String) {
        val currentDate: String = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val currentTime: String = SimpleDateFormat("HH:mm a", Locale.getDefault()).format(Date())
        val map = hashMapOf<String,String>()
        map["message"] = message
        map["senderId"] = senderId!!
        map["currentDate"] = currentDate
        map["currentTime"] = currentTime

        val reference=FirebaseDatabase.getInstance().getReference("Chats").child(chatId!!)
        reference.push().setValue(map)
            .addOnCompleteListener { messageSent->
//                FirebaseDatabase.getInstance().getReference("Users").child(senderId!!)
//                    .addListenerForSingleValueEvent(object : ValueEventListener{
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            if(snapshot.exists()){
//                                val userData = snapshot.getValue(UserModel::class.java)
//                            }
//                        }
//                        override fun onCancelled(error: DatabaseError) {
//                            TODO("Not yet implemented")
//                        }
//                    })
                sendNotification(message)
                if(messageSent.isSuccessful) {
                    Toast.makeText(this,"Message Sent",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this,messageSent.exception.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        verifyChatId()
    }

    private fun sendNotification(message: String) {
        FirebaseDatabase.getInstance().getReference("Users").child(receiverId!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val receiverData = snapshot.getValue(UserModel::class.java)
                if(snapshot.exists()){
                    FirebaseDatabase.getInstance().getReference("Users").child(senderId!!)
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val senderUser = snapshot.getValue(UserModel::class.java)
                                val notificationData = PushNotification(NotificationData(senderUser?.name!!,message), receiverData!!.fcmToken!!)
                                ApiUtilities.api.sendNotification(notificationData).enqueue(object : Callback<PushNotification>{
                                    override fun onResponse(
                                        call: Call<PushNotification>,
                                        response: Response<PushNotification>
                                    ) {
                                        if(response.isSuccessful){
                                            Toast.makeText(this@MessageActivity,"Notification Sent", Toast.LENGTH_SHORT).show()
                                        }
                                        else
                                            Toast.makeText(this@MessageActivity,response.errorBody().toString(), Toast.LENGTH_SHORT).show()
                                    }

                                    override fun onFailure(call: Call<PushNotification>, t: Throwable) {
                                        Toast.makeText(this@MessageActivity,"Something went wrong", Toast.LENGTH_SHORT).show()
                                    }

                                })
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })

                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MessageActivity,error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
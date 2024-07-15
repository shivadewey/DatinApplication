package com.example.datingapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.datingapp.databinding.ItemKolodaBinding
import com.example.datingapp.model.UserModel
import com.example.datingapp.ui.activity.MainActivity
import com.example.datingapp.ui.activity.MessageActivity
import com.example.datingapp.ui.fragments.DatingFragment

class DatingAdapter(val context: Context):RecyclerView.Adapter<DatingAdapter.UserViewHolder>() {

    private  var userDetail = ArrayList<UserModel>()
    fun setUserDetail(userDetail: ArrayList<UserModel>){
        this.userDetail = userDetail
        notifyDataSetChanged()
    }

    class UserViewHolder(val binding:ItemKolodaBinding):ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(ItemKolodaBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
       val details = userDetail[position]

        holder.binding.apply {
            Glide.with(holder.itemView).load(details.image).into(userImage)
            userName.text = details.name
            userEmail.text = details.email
            chatCard.setOnClickListener {
                val intent = Intent(context,MessageActivity::class.java)
                intent.putExtra("userId",details.number)
                context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return userDetail.size
    }
}
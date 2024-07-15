package com.example.datingapp.model

import android.net.Uri

data class UserModel(
    val number: String ?=null,
    val name: String ?=null,
    val email: String ?=null,
    val city: String? =null,
    val gender: String ?=null,
    val relationship: String ?=null,
    var fcmToken: String ?=null,
    val star: String ?=null,
    val image: String ?=null,
    val age: String ?=null,
    val status: String ?=null

        ){
}
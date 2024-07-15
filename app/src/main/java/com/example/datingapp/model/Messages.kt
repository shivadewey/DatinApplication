package com.example.datingapp.model

data class Messages(
    val message : String ?=null,
    val senderId : String ?= null,
    val currentTime : String ?= null,
    val currentDate : String ?= null
)
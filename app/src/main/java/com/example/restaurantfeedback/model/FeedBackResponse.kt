package com.example.restaurantfeedback.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class FeedBackResponse
{
    @SerializedName("success")
    @Expose
     val success: Boolean? = null

//    @SerializedName("data")
//    @Expose
//    private val data: Data? = null

    @SerializedName("message")
    @Expose
     val message: String? = null
}
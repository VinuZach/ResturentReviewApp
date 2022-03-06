package com.example.restaurantfeedback.Repository

import com.example.restaurantfeedback.model.FeedBackResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiEndPoints
{

    @POST("feedback")
    @Multipart
    fun sendFeedBack(@Header("Authorization") authHeader: String, @Part part: MutableList<MultipartBody.Part>) :Call<FeedBackResponse>

}
package com.example.restaurantfeedback.Repository

import android.util.Log
import com.example.restaurantfeedback.ANSWERTYPE_RATING_WITH_SUGGESTION
import com.example.restaurantfeedback.ANSWERTYPE_USER_ENTRY
import com.example.restaurantfeedback.DOUBLE_QUOTES
import com.example.restaurantfeedback.QuestionsDetails
import com.example.restaurantfeedback.model.FeedBackResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RetrofitMethods : RetrofitManger<ApiEndPoints>()
{

    override var baseUrl: String = "https://teamcatchacloud.com/feedback/api/"
    override var apiEndPoint: Class<ApiEndPoints> = ApiEndPoints::class.java
    private const val TAG = "RetrofitMethods12321"

    val token = "Bearer 2|WjWql5ixV0gSUbxiJaJK9I6XL8KTQw9OfHxSRRz8"
    fun sendFeedBack(questionList: MutableList<QuestionsDetails>, customer_name: String, customer_email: String, customer_phone: String,
            apiResponse: ApiResponse)
    {
        val fieldMap: MutableList<MultipartBody.Part> = mutableListOf()
        fieldMap.add(createStringMultiPartBody("title", "" + (0..10).random()))

        fieldMap.add(createStringMultiPartBody("description", "1"))
        fieldMap.add(createStringMultiPartBody("name", customer_name))
        fieldMap.add(createStringMultiPartBody("email", customer_email))
        fieldMap.add(createStringMultiPartBody("number", customer_phone))
        var questionanswerValue = ""
        for (item in questionList)
        {

            if (!item.isSkiped)
            {

                when (item.questionId)
                {
                    6, 5 ->
                    {
                    }
                    else ->
                    {
                        when (item.answerType)
                        {
                            ANSWERTYPE_USER_ENTRY, ANSWERTYPE_RATING_WITH_SUGGESTION ->
                            {
                                if (questionanswerValue.isNotEmpty()) questionanswerValue = questionanswerValue + "," +  item.answer
                                else questionanswerValue =  item.answer
                                Log.i(TAG, "sendFeedBack: questionanswerValue "+item.answer)
                            }
                            else                                                     ->
                            {
                                if (questionanswerValue.isNotEmpty()) questionanswerValue = questionanswerValue + "," +DOUBLE_QUOTES + item.question + DOUBLE_QUOTES + ":" + DOUBLE_QUOTES + item.answer +DOUBLE_QUOTES
                                else questionanswerValue =DOUBLE_QUOTES + item.question + DOUBLE_QUOTES + ":" + DOUBLE_QUOTES + item.answer + DOUBLE_QUOTES
                            }
                        }
                    }
                }

            }
        }
        questionanswerValue = "{" + questionanswerValue + "}"
        Log.d(TAG, "sendFeedBack: " + questionanswerValue)

        fieldMap.add(createStringMultiPartBody("questionanswer", questionanswerValue))


        retrofitBuilder.sendFeedBack(token, fieldMap).enqueue(object : Callback<FeedBackResponse>
        {
            override fun onResponse(call: Call<FeedBackResponse>, response: Response<FeedBackResponse>)
            {

                if(response.isSuccessful)
                {
                    apiResponse.onResponseObtained(true,response.body()?.message)
                }
                else
                    apiResponse.onResponseObtained(false,"FeedBack not send")
            }

            override fun onFailure(call: Call<FeedBackResponse>, t: Throwable)
            {
                apiResponse.onResponseObtained(false,"Server not available")
            }

        })
    }

}
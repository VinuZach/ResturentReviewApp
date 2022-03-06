package com.example.restaurantfeedback

import androidx.annotation.DrawableRes

val ANSWERTYPE_RATING=1
val ANSWERTYPE_TWOOPTION=2
val ANSWERTYPE_USER_ENTRY=3
val ANSWERTYPE_RATING_NOIMAGE=4
val ANSWERTYPE_RATING_WITH_SUGGESTION=5
data class QuestionsDetails(@DrawableRes var imageDrawable:Int,var questionId:Int,var question:String,var answerType:Int,var answer:String,var isAnswered:Boolean=false,var multipleOptions:MutableList<String> = mutableListOf()
                           ,var optionImageList:MutableList<Int> = mutableListOf(),var isSkiped:Boolean=false,var selectedRating:String="")

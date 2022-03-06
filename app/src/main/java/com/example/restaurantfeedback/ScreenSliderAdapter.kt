package com.example.restaurantfeedback

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ScreenSliderAdapter(fragmentActivity: FragmentActivity, val questionList: MutableList<QuestionsDetails>) :
        FragmentStateAdapter(fragmentActivity)
{
    override fun getItemCount(): Int
    {
        return questionList.size
    }

    override fun createFragment(position: Int): Fragment = QuestionsFragment(questionList.get(position))

}
package com.example.restaurantfeedback

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class QuestionsFragment(val questionDetails: QuestionsDetails) : Fragment()
{
    private val TAG = "QuestionsFragment123"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {

        val view = inflater.inflate(R.layout.fragment_questions, container, false)
        val questionTextView = view.findViewById<TextView>(R.id.questiontext)
        val imageView = view.findViewById<ImageView>(R.id.image)
        imageView.setImageResource(questionDetails.imageDrawable);
        if(questionDetails.imageDrawable==R.drawable.fooditem_yellowstroke)
        {
            imageView.setPadding(100,100,100,100)
        }

        if(questionDetails.imageDrawable==R.drawable.ic_wallet)
        {
            imageView.setPadding(80,80,80,80)
        }
        questionTextView.text = questionDetails.question
        return view
    }


}
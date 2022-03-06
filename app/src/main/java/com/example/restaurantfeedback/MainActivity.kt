package com.example.restaurantfeedback

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.*
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.restaurantfeedback.Repository.RetrofitManger
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

val DOUBLE_QUOTES = "\""

class MainActivity : FragmentActivity(), View.OnClickListener
{
    lateinit var questionsViewPager: ViewPager2
    lateinit var tabLayout: DotsIndicator
    lateinit var backImageView: ImageView
    val questionList: MutableList<QuestionsDetails> = mutableListOf()


    lateinit var ratingLinearLayout: LinearLayout
    lateinit var twoOptionsLinearLayout: LinearLayout
    lateinit var userEntryLinearLayout: LinearLayout
    lateinit var nextButton: Button
    lateinit var screenSliderAdapter: ScreenSliderAdapter


    //rating
    lateinit var rating_poorParLinearLayout: LinearLayout
    lateinit var rating_okParLinearLayout: LinearLayout
    lateinit var rating_goodParLinearLayout: LinearLayout
    lateinit var rating_execlParLinearLayout: LinearLayout


    lateinit var rating_suggestion_parent: TextInputLayout
    lateinit var rating_suggestionTextInputEditText: TextInputEditText

    // two option layout
    lateinit var twoOption_option1: TextView
    lateinit var twoOption_option2: TextView

    lateinit var twoOption_option1ImageView: ImageView
    lateinit var twoOption_option2ImageView: ImageView

    //userEntry

    lateinit var userEntry_entry1_parent: TextInputLayout
    lateinit var userEntry_entry2_parent: TextInputLayout
    lateinit var userEntry_entry3_parent: TextInputLayout

    lateinit var userEntry_entry1: TextInputEditText
    lateinit var userEntry_entry2: TextInputEditText
    lateinit var userEntry_entry3: TextInputEditText

    var customer_name = ""
    var customer_email = ""
    var customer_phone = ""
    lateinit var answerLinearLayout: RelativeLayout

    var toCloseApplication = false
    lateinit var skipQuestionLinearLayout: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init();



        questionList.add(QuestionsDetails(R.drawable.fooditem_yellowstroke, 1, getString(R.string.foodquality), ANSWERTYPE_RATING, "",
            multipleOptions = mutableListOf(getString(R.string.foodquality))))

        questionList.add(QuestionsDetails(R.drawable.ic_wallet, 2, getString(R.string.foodreview), ANSWERTYPE_RATING_WITH_SUGGESTION, "",
            multipleOptions = mutableListOf("Is there anything we can improve")))

        questionList.add(QuestionsDetails(R.drawable.ambience, 3, getString(R.string.ambiencerate), ANSWERTYPE_RATING, ""))
        questionList.add(QuestionsDetails(R.drawable.rating, 4, getString(R.string.servicerate), ANSWERTYPE_RATING, ""))
        questionList.add(
            QuestionsDetails(R.drawable.ic_socialmedia, 5, getString(R.string.socialreview), ANSWERTYPE_TWOOPTION, "", isAnswered = true,
                arrayListOf("FaceBook", "Instagram"), optionImageList = mutableListOf(R.drawable.facebook_icon, R.drawable.instagram_icon)))
        questionList.add(QuestionsDetails(R.drawable.ic_mobile, 6, getString(R.string.upcomming), ANSWERTYPE_USER_ENTRY, "",
            multipleOptions = arrayListOf("Your Name", "Phone", "Email")))

        questionList.add(QuestionsDetails(R.drawable.ic_location, 7, getString(R.string.visitagain), ANSWERTYPE_TWOOPTION, "",
            multipleOptions = arrayListOf("Yes", "No"),
            optionImageList = arrayListOf(R.drawable.yesicon_selecto, R.drawable.noicon_selector)))


        screenSliderAdapter = ScreenSliderAdapter(this, questionList)
        questionsViewPager.adapter = screenSliderAdapter
        tabLayout.setViewPager2(questionsViewPager)
        questionsViewPager.setPageTransformer(QuestionViewPagerAnimation())

        setAnswerLayout(0)

        questionsViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback()
        {
            override fun onPageSelected(position: Int)
            {
                super.onPageSelected(position)
                screenSliderAdapter.questionList.get(questionsViewPager.currentItem).isSkiped = false
                if (position == 0) backImageView.visibility = View.GONE
                else backImageView.visibility = View.VISIBLE

                setAnswerLayout(position)
            }
        })

        backImageView.setOnClickListener {

            if ((questionsViewPager.currentItem - 1) >= 0)
            {
                questionsViewPager.currentItem = questionsViewPager.currentItem - 1
            }
            else
            {
                checkUserToClose()
            }
        }

        skipQuestionLinearLayout.setOnClickListener {
            screenSliderAdapter.questionList.get(questionsViewPager.currentItem).isSkiped = true
            nextButton.callOnClick()
        }
        nextButton.setOnClickListener {

            if (screenSliderAdapter.questionList.get(questionsViewPager.currentItem).isSkiped)
            {
                nextQuestOrSend()
            }
            else
            {

                val currentQuestion = screenSliderAdapter.questionList.get(questionsViewPager.currentItem)

                if (currentQuestion.answerType == ANSWERTYPE_USER_ENTRY)
                {
                    var isFullyAns = false
                    var fullText = ""
                    for (index in 0..currentQuestion.multipleOptions.size - 1)
                    {
                        if (userEntryparent_ids.get(index).visibility == View.VISIBLE)
                        {

                            if (userEntry_ids.get(index).text.toString().isNotEmpty())
                            {


                                Log.i("asdsdwe", "onCreate: hint " + userEntryparent_ids.get(index).hint.toString())
                                Log.i("asdsdwe", "onCreate: text " + userEntry_ids.get(index).text.toString())
                                when (userEntryparent_ids.get(index).hint.toString())
                                {
                                    "Your Name" -> customer_name = userEntry_ids.get(index).text.toString()
                                    "Phone"     -> customer_phone = userEntry_ids.get(index).text.toString()
                                    "Email"     -> customer_email = userEntry_ids.get(index).text.toString()
                                }

                                if (fullText.isNotEmpty()) fullText = fullText + "," + DOUBLE_QUOTES + userEntryparent_ids.get(
                                    index).hint.toString() + DOUBLE_QUOTES + ":" + userEntry_ids.get(index).text.toString() + DOUBLE_QUOTES
                                else fullText = DOUBLE_QUOTES + userEntryparent_ids.get(
                                    index).hint.toString() + DOUBLE_QUOTES + ":" + DOUBLE_QUOTES + userEntry_ids.get(
                                    index).text.toString() + DOUBLE_QUOTES


                                isFullyAns = true
                            }
                            else isFullyAns = false
                        }
                    }
                    if (isFullyAns)
                    {

                        screenSliderAdapter.questionList.get(questionsViewPager.currentItem).isAnswered = true
                        screenSliderAdapter.questionList.get(questionsViewPager.currentItem).answer = fullText
                    }
                    else screenSliderAdapter.questionList.get(questionsViewPager.currentItem).isAnswered = false
                }

                if (currentQuestion.answerType == ANSWERTYPE_RATING_WITH_SUGGESTION)
                {
                    if (rating_suggestionTextInputEditText.text.toString().isNotEmpty())
                    {
                        val fullText = DOUBLE_QUOTES + currentQuestion.question + DOUBLE_QUOTES + ":" + DOUBLE_QUOTES + currentQuestion.answer + DOUBLE_QUOTES + "," + DOUBLE_QUOTES + rating_suggestion_parent.hint.toString() + DOUBLE_QUOTES + ":" + DOUBLE_QUOTES + rating_suggestionTextInputEditText.text.toString() + DOUBLE_QUOTES
                        screenSliderAdapter.questionList.get(questionsViewPager.currentItem).answer = fullText
                        screenSliderAdapter.questionList.get(questionsViewPager.currentItem).isAnswered = true
                    }
                    else
                    {
                        if (screenSliderAdapter.questionList.get(questionsViewPager.currentItem).isAnswered)
                        {
                            val fullText = DOUBLE_QUOTES + currentQuestion.question + DOUBLE_QUOTES + ":" + DOUBLE_QUOTES + currentQuestion.answer + DOUBLE_QUOTES
                            screenSliderAdapter.questionList.get(questionsViewPager.currentItem).answer = fullText
                        }
                    }


                }
                if (screenSliderAdapter.questionList.get(questionsViewPager.currentItem).isAnswered)
                {
                    nextQuestOrSend()

                }
                else Toast.makeText(this, "Response required", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun nextQuestOrSend()
    {

        if ((questionsViewPager.currentItem + 1) < questionList.size)
        {

            questionsViewPager.currentItem = questionsViewPager.currentItem + 1

        }
        else
        {

            Log.d("asdsdwe", "onCreate: " + screenSliderAdapter.questionList)

            showCustomLayoutDialog(R.layout.dialog_completepost, object : onDialogCreated
            {
                override fun onViewCreated(dialog: Dialog)
                {

                    val headerTextView = dialog.findViewById<TextView>(R.id.dialogtitle)
                    val messageTextView = dialog.findViewById<TextView>(R.id.message)
                    val progressBar = dialog.findViewById<ProgressBar>(R.id.dialogprogress)

                    headerTextView.text = getString(R.string.sending_review)
                    messageTextView.text = getString(R.string.your_review_is_being_send_n_n_please_wait)
                    progressBar.visibility=View.VISIBLE

                    RepositoryManager.retrofitObject.sendFeedBack(screenSliderAdapter.questionList, customer_name, customer_email,
                        customer_phone, object : RetrofitManger.ApiResponse
                        {
                            override fun onResponseObtained(isSuccess: Boolean, responseData: Any?)
                            {
                                progressBar.visibility=View.GONE
                                if (isSuccess)
                                {
                                    customer_name = ""
                                    customer_email = ""
                                    customer_phone = ""

                                    headerTextView.text = getString(R.string.review_send_successfully)
                                    messageTextView.text = getString(R.string.your_review_has_been_send_successfully_n_nthank_you)

                                }
                                else
                                {
                                    headerTextView.text = getString(R.string.review_not_send)
                                    headerTextView.setBackgroundColor(ContextCompat.getColor(this@MainActivity,R.color.lightred))
                                    messageTextView.text = getString(R.string.review_not_send_messg)
                                }
                                Handler(Looper.getMainLooper()).postDelayed({
                                    if (isSuccess)
                                    {
                                        startActivity(Intent(this@MainActivity, SplashScreenActivity::class.java))
                                        finish()
                                    }
                                    dialog.cancel()

                                }, 3000)
                            }

                        })
                }
            })

        }
    }

    private fun showCustomLayoutDialog(@LayoutRes layoutRes: Int, onDialogCreated: onDialogCreated)
    {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(layoutRes)

        dialog.show()

        val window: Window? = dialog.window
        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        onDialogCreated.onViewCreated(dialog)

    }

    interface onDialogCreated
    {
        fun onViewCreated(dialog: Dialog)
    }

    private fun setAnswerLayout(position: Int)
    {
        ratingLinearLayout.visibility = View.GONE
        twoOptionsLinearLayout.visibility = View.GONE
        userEntryLinearLayout.visibility = View.GONE

        rating_suggestion_parent.visibility = View.GONE
        nextButton.visibility = View.GONE
        rating_suggestionTextInputEditText.text?.clear()
        skipQuestionLinearLayout.visibility = View.VISIBLE
        userEntryparent_ids.forEach {
            it.editText?.text?.clear()
            it.visibility = View.GONE
        }

        when (questionList[position].answerType)
        {
            ANSWERTYPE_RATING_NOIMAGE, ANSWERTYPE_RATING, ANSWERTYPE_RATING_WITH_SUGGESTION ->
            {
                ratingLinearLayout.visibility = View.VISIBLE
                rating_poorParLinearLayout.isSelected = false
                rating_okParLinearLayout.isSelected = false
                rating_goodParLinearLayout.isSelected = false
                rating_execlParLinearLayout.isSelected = false

                if (questionList[position].answerType == ANSWERTYPE_RATING_NOIMAGE)
                {
                    findViewById<ImageView>(R.id.poorimage).visibility = View.INVISIBLE
                    findViewById<ImageView>(R.id.emotionless).visibility = View.INVISIBLE
                    findViewById<ImageView>(R.id.good).visibility = View.INVISIBLE
                    findViewById<ImageView>(R.id.exec).visibility = View.INVISIBLE
                }
                else
                {
                    findViewById<ImageView>(R.id.poorimage).visibility = View.VISIBLE
                    findViewById<ImageView>(R.id.emotionless).visibility = View.VISIBLE
                    findViewById<ImageView>(R.id.good).visibility = View.VISIBLE
                    findViewById<ImageView>(R.id.exec).visibility = View.VISIBLE
                }

                Log.d("asdsads", "setAnswerLayout: " + questionList[position].answer)
                if (questionList[position].answer.isNotEmpty())
                {
                    val prevAnswer: String
                    if (questionList[position].answerType == ANSWERTYPE_RATING_WITH_SUGGESTION) prevAnswer = questionList[position].selectedRating
                    else prevAnswer = questionList[position].answer
                    when (prevAnswer)
                    {
                        POOR_RATING_VALUE    ->
                        {
                            rating_poorParLinearLayout.isSelected = true
                            if (questionList[position].answerType == ANSWERTYPE_RATING_WITH_SUGGESTION) rating_suggestion_parent.visibility = View.VISIBLE
                        }
                        OK_RATING_VALUE      ->
                        {
                            rating_okParLinearLayout.isSelected = true
                            if (questionList[position].answerType == ANSWERTYPE_RATING_WITH_SUGGESTION) rating_suggestion_parent.visibility = View.VISIBLE
                        }

                        GOOD_RATING_VALUE    ->
                        {
                            rating_goodParLinearLayout.isSelected = true
                            rating_goodParLinearLayout.isSelected = true
                        }
                        EXELENT_RATING_VALUE ->
                        {
                            rating_execlParLinearLayout.isSelected = true
                            rating_execlParLinearLayout.isSelected = true
                        }
                    }
                    if(rating_suggestion_parent.visibility== View.VISIBLE)
                        nextButton.visibility = View.VISIBLE
                }
            }
            ANSWERTYPE_TWOOPTION                                                            ->
            {
                twoOptionsLinearLayout.visibility = View.VISIBLE
                twoOption_option1.text = questionList[position].multipleOptions[0]
                twoOption_option2.text = questionList[position].multipleOptions[1]
                twoOption_option1ImageView.isSelected = false
                twoOption_option2ImageView.isSelected = false

                twoOption_option1ImageView.setImageResource(questionList[position].optionImageList[0])
                twoOption_option2ImageView.setImageResource(questionList[position].optionImageList[1])
                if (questionList[position].questionId == 5)
                {
                    nextButton.visibility=View.VISIBLE
                    twoOption_option1.tag = "https://www.facebook.com/groups/2340998672696028/?multi_permalinks=4629067813889091"
                    twoOption_option2.tag = "https://www.facebook.com/groups/2340998672696028/?multi_permalinks=4629067813889091"
                }
                else
                {
                    nextButton.visibility=View.GONE
                    twoOption_option1.tag = ""
                    twoOption_option2.tag = ""
                }
            }
            ANSWERTYPE_USER_ENTRY                                                           ->
            {
                nextButton.visibility=View.VISIBLE

                userEntryLinearLayout.visibility = View.VISIBLE
                if (questionList[position].questionId == 6) skipQuestionLinearLayout.visibility = View.GONE
                for (i in 0..questionList[position].multipleOptions.size - 1)
                {
                    if (questionList[position].multipleOptions.get(i).isNotEmpty())
                    {
                        userEntryparent_ids.get(i).visibility = View.VISIBLE
                        userEntryparent_ids.get(i).hint = questionList[position].multipleOptions.get(i)
                        if (questionList[position].multipleOptions.get(i).contains("Phone"))
                        {
                            userEntry_ids.get(i).inputType = InputType.TYPE_CLASS_PHONE
                        }
                        else if (questionList[position].multipleOptions.get(i).contains("Email"))
                        {
                            userEntry_ids.get(i).inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        }
                        else userEntry_ids.get(i).inputType = InputType.TYPE_CLASS_TEXT
                    }
                    else
                    {

                        userEntryparent_ids.get(i).visibility = View.GONE
                    }
                }

            }
        }
    }

    var userEntryparent_ids: MutableList<TextInputLayout> = mutableListOf()
    var userEntry_ids: MutableList<TextInputEditText> = mutableListOf()


    fun init()
    {
        questionsViewPager = findViewById(R.id.questionviewpage)
        questionsViewPager.isUserInputEnabled = false
        tabLayout = findViewById(R.id.tablayout)
        backImageView = findViewById(R.id.backpress)
        ratingLinearLayout = findViewById(R.id.rating)
        twoOptionsLinearLayout = findViewById(R.id.twooptions)
        answerLinearLayout = findViewById(R.id.relativeLayout)
        skipQuestionLinearLayout = findViewById(R.id.skipquestion)
        userEntryLinearLayout = findViewById(R.id.entry)
        nextButton = findViewById(R.id.nextbutton)



        rating_poorParLinearLayout = ratingLinearLayout.findViewById(R.id.poorparent)
        rating_okParLinearLayout = ratingLinearLayout.findViewById(R.id.okparent)
        rating_goodParLinearLayout = ratingLinearLayout.findViewById(R.id.goodparent)
        rating_execlParLinearLayout = ratingLinearLayout.findViewById(R.id.exeparent)

        rating_suggestion_parent = ratingLinearLayout.findViewById(R.id.suggestionparent)
        rating_suggestionTextInputEditText = ratingLinearLayout.findViewById(R.id.suggestionentr)

        twoOption_option1 = twoOptionsLinearLayout.findViewById(R.id.option1)
        twoOption_option2 = twoOptionsLinearLayout.findViewById(R.id.option2)

        twoOption_option1ImageView = twoOptionsLinearLayout.findViewById(R.id.option1image)
        twoOption_option2ImageView = twoOptionsLinearLayout.findViewById(R.id.option2image)


        userEntry_entry1_parent = userEntryLinearLayout.findViewById(R.id.entry1_parent)
        userEntry_entry2_parent = userEntryLinearLayout.findViewById(R.id.entry2_parent)
        userEntry_entry3_parent = userEntryLinearLayout.findViewById(R.id.entry3_parent)



        userEntry_entry1 = userEntryLinearLayout.findViewById(R.id.entry1)
        userEntry_entry2 = userEntryLinearLayout.findViewById(R.id.entry2)
        userEntry_entry3 = userEntryLinearLayout.findViewById(R.id.entry3)


        userEntry_entry3_parent = userEntryLinearLayout.findViewById(R.id.entry3_parent)

        userEntryparent_ids.add(userEntry_entry1_parent)
        userEntryparent_ids.add(userEntry_entry2_parent)
        userEntryparent_ids.add(userEntry_entry3_parent)

        userEntry_ids.add(userEntry_entry1)
        userEntry_ids.add(userEntry_entry2)
        userEntry_ids.add(userEntry_entry3)

        rating_poorParLinearLayout.setOnClickListener(this)
        rating_okParLinearLayout.setOnClickListener(this)
        rating_goodParLinearLayout.setOnClickListener(this)
        rating_execlParLinearLayout.setOnClickListener(this)

        twoOption_option1.setOnClickListener(this)
        twoOption_option2.setOnClickListener(this)
        findViewById<LinearLayout>(R.id.option1parent).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.option2parent).setOnClickListener(this)
    }

    val POOR_RATING_VALUE = "poor"
    val OK_RATING_VALUE = "Ok"
    val GOOD_RATING_VALUE = "good"
    val EXELENT_RATING_VALUE = "excellent"

    override fun onClick(view: View?)
    {
        var selectedAnswer = ""

        rating_poorParLinearLayout.isSelected = false
        rating_okParLinearLayout.isSelected = false
        rating_goodParLinearLayout.isSelected = false
        rating_execlParLinearLayout.isSelected = false

        twoOption_option1ImageView.isSelected = false
        twoOption_option2ImageView.isSelected = false

        view?.let {
            it.isSelected = true
            when (it.id)
            {

                rating_poorParLinearLayout.id            ->
                {
                    selectedAnswer = POOR_RATING_VALUE
                    checkSuggestionBocx()

                }
                rating_okParLinearLayout.id              ->
                {
                    selectedAnswer = OK_RATING_VALUE
                    checkSuggestionBocx()
                }

                rating_goodParLinearLayout.id            ->
                {
                    selectedAnswer = GOOD_RATING_VALUE
                    rating_suggestion_parent.visibility = View.GONE
                    nextButton.visibility=View.GONE
                    rating_suggestionTextInputEditText.text?.clear()
                }
                rating_execlParLinearLayout.id           ->
                {
                    selectedAnswer = EXELENT_RATING_VALUE
                    rating_suggestion_parent.visibility = View.GONE
                    nextButton.visibility=View.GONE
                    rating_suggestionTextInputEditText.text?.clear()
                }

                R.id.option2parent, twoOption_option2.id ->
                {
                    twoOption_option2ImageView.isSelected = true
                    if (twoOption_option2.tag.toString().isNotEmpty())
                    {

                        showQrCodeDialog(twoOption_option2)


                    }
                    else selectedAnswer = twoOption_option2.text.toString()
                }
                R.id.option1parent, twoOption_option1.id ->
                {
                    twoOption_option1ImageView.isSelected = true
                    if (twoOption_option1.tag.toString().isNotEmpty())
                    {
                        showQrCodeDialog(twoOption_option1)
                    }
                    else selectedAnswer = twoOption_option1.text.toString()
                }
            }

            if (selectedAnswer.isNotEmpty())
            {
                val currentQuestion = screenSliderAdapter.questionList.get(questionsViewPager.currentItem)
                if (currentQuestion.answerType == ANSWERTYPE_RATING_WITH_SUGGESTION)
                {
                    currentQuestion.selectedRating = selectedAnswer
                    if ((selectedAnswer == POOR_RATING_VALUE || selectedAnswer == OK_RATING_VALUE)) screenSliderAdapter.questionList.get(
                        questionsViewPager.currentItem).isAnswered = false
                    else screenSliderAdapter.questionList.get(questionsViewPager.currentItem).isAnswered = true
                }
                else screenSliderAdapter.questionList.get(questionsViewPager.currentItem).isAnswered = true

                screenSliderAdapter.questionList.get(questionsViewPager.currentItem).answer = selectedAnswer

                if (nextButton.visibility==View.GONE)
                {

                    Handler(Looper.getMainLooper()).postDelayed({
                        nextButton.callOnClick()

                    }, 500)
                }
            }
        }

    }

    private fun checkSuggestionBocx()
    {
        val currentQuestion = screenSliderAdapter.questionList.get(questionsViewPager.currentItem)
        if (currentQuestion.answerType == ANSWERTYPE_RATING_WITH_SUGGESTION)
        {
            rating_suggestion_parent.visibility = View.VISIBLE
            rating_suggestion_parent.hint = currentQuestion.multipleOptions[0]
            nextButton.visibility=View.VISIBLE
        }
        else rating_suggestion_parent.visibility = View.GONE


    }

    private fun showQrCodeDialog(twooptionOption2: TextView)
    {
        val dialog = BottomSheetDialog(this)
        val bottomSheet = layoutInflater.inflate(R.layout.bottom_sheet, null)
        val imageView = bottomSheet.findViewById<ImageView>(R.id.qrcode)
        val title = bottomSheet.findViewById<TextView>(R.id.text)
        bottomSheet.findViewById<ImageView>(R.id.backpress).setOnClickListener {
            dialog.cancel()
        }
        title.setText(twooptionOption2.text)
        try
        {
            val bitmap = encodeAsBitmap(twooptionOption2.toString())
            imageView.setImageBitmap(bitmap)
            dialog.setContentView(bottomSheet)
            dialog.show()
        } catch (e: WriterException)
        {
            e.printStackTrace()
        }
    }

    val WHITE = -0x1
    val BLACK = -0x1000000
    val WIDTH = 800

    @Throws(WriterException::class)
    fun encodeAsBitmap(str: String?): Bitmap?
    {
        val result: BitMatrix
        result = try
        {
            MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, WIDTH, WIDTH, null)
        } catch (iae: IllegalArgumentException)
        {
            // Unsupported format
            return null
        }
        val w = result.width
        val h = result.height
        val pixels = IntArray(w * h)
        for (y in 0 until h)
        {
            val offset = y * w
            for (x in 0 until w)
            {
                pixels[offset + x] = if (result[x, y]) BLACK else WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
        return bitmap
    }

    override fun onBackPressed()
    {

        if ((questionsViewPager.currentItem - 1) >= 0) backImageView.callOnClick()
        else checkUserToClose()
    }

    fun checkUserToClose()
    {
        if (toCloseApplication)
        {
            finish()
        }
        else
        {
            Toast.makeText(this, "Press again to close", Toast.LENGTH_SHORT).show()
            toCloseApplication = true
        }
    }


}
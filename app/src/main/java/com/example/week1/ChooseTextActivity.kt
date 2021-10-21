package com.example.week1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ChooseTextActivity : AppCompatActivity() {
    private val extraName = "reply"

    private lateinit var firstTextView: TextView
    private lateinit var secondTextView: TextView
    private lateinit var thirdTextView: TextView
    private lateinit var fourthTextView: TextView
    private lateinit var fifthTextView: TextView
    private lateinit var sixthTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_text)
        initViews()
        setListeners()
    }

    private fun initViews() {
        firstTextView = findViewById(R.id.first_tv)
        secondTextView = findViewById(R.id.second_tv)
        thirdTextView = findViewById(R.id.third_tv)
        fourthTextView = findViewById(R.id.fourth_tv)
        fifthTextView = findViewById(R.id.fifth_tv)
        sixthTextView = findViewById(R.id.sixth_tv)
    }

    private fun returnReply(data: String) {
        val replyIntent = Intent()
        replyIntent.putExtra(extraName, data)
        setResult(RESULT_OK, replyIntent)
        finish()
    }

    fun setListeners() {
        firstTextView.setOnClickListener {
            returnReply(firstTextView.text.toString())
        }
        secondTextView.setOnClickListener {
            returnReply(secondTextView.text.toString())
        }
        thirdTextView.setOnClickListener {
            returnReply(thirdTextView.text.toString())
        }
        fourthTextView.setOnClickListener {
            returnReply(fourthTextView.text.toString())
        }
        fifthTextView.setOnClickListener {
            returnReply(fifthTextView.text.toString())
        }
        sixthTextView.setOnClickListener {
            returnReply(sixthTextView.text.toString())
        }
    }
}
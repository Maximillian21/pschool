package com.example.week1

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {
    private val prefKey = "count"
    private val extraName = "reply"
    private val sharedPrefFile = "sharedPreference"

    private lateinit var transmittedTextView: TextView
    private lateinit var chosenTextView: TextView
    private lateinit var entryCount: TextView
    private lateinit var chooseBtn: Button
    private lateinit var shareButton: Button
    private lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        setListeners()
        updateEntryCount()
        startForResult = registerForActivityResult(ActivityResultContracts
            .StartActivityForResult()) {
            if(it.resultCode == RESULT_OK) {
                val text = it.data?.getStringExtra(extraName)
                chosenTextView.text = text
            }
        }
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                handleSendText(intent)
            }
        }
    }

    private fun initViews() {
        transmittedTextView = findViewById(R.id.received_tv)
        chosenTextView = findViewById(R.id.chosen_tv)
        entryCount = findViewById(R.id.entry_count_tv)
        chooseBtn = findViewById(R.id.choose_button)
        shareButton = findViewById(R.id.share_button)
    }

    private fun setListeners() {
        shareButton.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, chosenTextView.text)
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, null))
        }
        chooseBtn.setOnClickListener {
            startForResult.launch(Intent(this, ChooseTextActivity::class.java))
        }
    }

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            transmittedTextView.text = it
            transmittedTextView.visibility = View.VISIBLE
        }
    }

    private fun updateEntryCount() {
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE)
        var count = sharedPreferences.getInt(prefKey, 1)
        entryCount.text = count.toString()
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt(prefKey, ++count)
        editor.apply()
    }
}
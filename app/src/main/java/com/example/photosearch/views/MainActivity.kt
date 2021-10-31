package com.example.photosearch.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.util.Linkify
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import com.example.photosearch.R
import com.example.photosearch.data.Photo

import android.content.Intent
import android.net.Uri
import android.text.Spannable

import android.text.style.ClickableSpan

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.example.photosearch.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {
    private val DOMAIN_NAME = "www.flickr.com/photos/"

    private val viewModel: MainViewModel by viewModels()
    private lateinit var linksTextView: TextView
    private lateinit var searchField: EditText
    private lateinit var findBtn: Button
    private lateinit var loadingView: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setViews()

        findBtn.setOnClickListener {
            if(!TextUtils.isEmpty(searchField.text.toString()))
                viewModel.refresh(searchField.text.toString())
        }

        observeViewModel()
    }

    private fun setViews() {
        linksTextView = findViewById(R.id.links_tv)
        searchField = findViewById(R.id.search_field)
        findBtn = findViewById(R.id.find_button)
        loadingView = findViewById(R.id.loadingView)
    }

    private fun observeViewModel() {
        viewModel.photosLinks.observe(this, { results ->
            results?.let {
                linksTextView.visibility = View.VISIBLE
                linksTextView.text = createLinksString(it.photos.photo)
                Linkify.addLinks(linksTextView, Linkify.WEB_URLS)
            }
        })

        viewModel.loading.observe(this, { isLoading ->
            isLoading?.let {
                loadingView.visibility = if(it) View.VISIBLE else View.GONE
                if(it) {
                    linksTextView.visibility = View.GONE
                }
            }
        })
    }

    private fun createLinksString(linksList: List<Photo>): Spannable {
        val resultSpannableText = SpannableStringBuilder()
        var index: Int
        for (element in linksList) {
            val totalString = DOMAIN_NAME + element.owner + "/" + element.id + "\n"
            resultSpannableText.append(totalString)
            index = resultSpannableText.indexOf(totalString)
            val clickableSpan: ClickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    val intent = Intent(applicationContext, WebViewActivity::class.java)
                    intent.putExtra("url", Uri.parse(totalString).toString())
                    startActivity(intent)
                }
            }
            resultSpannableText.setSpan(clickableSpan, index,index + totalString.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return resultSpannableText
    }
}


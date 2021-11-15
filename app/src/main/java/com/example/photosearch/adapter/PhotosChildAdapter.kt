package com.example.photosearch.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photosearch.R
import com.example.photosearch.data.Photo

class PhotosChildAdapter(
    private val onClick: (Photo) -> Unit
): BaseAdapter() {
    override fun getLayoutId(position: Int, obj: Photo): Int = R.layout.photo_item
    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return PhotosViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as PhotosViewHolder).bind(photosList[position])
    }
}

class PhotosViewHolder(
    itemView: View, val onClick: (Photo) -> Unit
) : RecyclerView.ViewHolder(itemView)  {

    private lateinit var item: Photo

    init {
        itemView.setOnClickListener {
            onClick(item)
        }
    }

    fun bind(photo: Photo) {
        itemView.apply {
            item = photo
            val photoLink = photo.photoLink
            Glide.with(findViewById<ImageView>(R.id.iv_photo_item))
                .load(photoLink)
                .into(findViewById(R.id.iv_photo_item))
            findViewById<TextView>(R.id.tv_search_result).text = photoLink
        }
    }
}
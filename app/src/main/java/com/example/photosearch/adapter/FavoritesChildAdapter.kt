package com.example.photosearch.adapter

import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photosearch.R
import com.example.photosearch.data.Photo
import com.example.photosearch.viewmodels.FavoritesViewModel

class FavoritesChildAdapter(
    private val viewModel: FavoritesViewModel,
    private val onClick: (Photo) -> Unit
): BaseAdapter() {
    override fun getLayoutId(position: Int, obj: Photo): Int = R.layout.favorite_item
    override fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder {
        return FavoritesChildViewHolder(view, onClick)
    }

    fun addData(newPhoto: Photo, position: Int) {
        photosList.add(newPhoto)
        notifyItemInserted(position)
    }

    override fun removeItem(position: Int) {
        viewModel.removePhoto(photosList[position])
        super.removeItem(position)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as FavoritesChildViewHolder).bind(photosList[position])
        holder.button.setOnClickListener {
            viewModel.removePhoto(photosList[position])
        }
    }
}

class FavoritesChildViewHolder(
    itemView: View, val onClick: (Photo) -> Unit
) : RecyclerView.ViewHolder(itemView)  {

    private lateinit var item: Photo
    lateinit var button: Button

    init {
        itemView.setOnClickListener {
            onClick(item)
        }
    }

    fun bind(photo: Photo) {
        itemView.apply {
            item = photo
            button = findViewById(R.id.btn_remove)
            val photoLink = photo.photoLink
            Glide.with(findViewById<ImageView>(R.id.iv_photo_favorite))
                .load(photoLink)
                .into(findViewById(R.id.iv_photo_favorite))
        }
    }
}
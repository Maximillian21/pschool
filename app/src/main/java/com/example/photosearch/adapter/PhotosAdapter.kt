package com.example.photosearch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photosearch.data.Photo
import com.example.photosearch.databinding.PhotoItemBinding

class PhotosAdapter(
    private val onClick: (Photo) -> Unit
): RecyclerView.Adapter<PhotosViewHolder>() {

    private  var photosList: MutableList<Photo> = mutableListOf()

    fun addData(newPhotosList :MutableList<Photo>) {
        photosList.addAll(newPhotosList)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        photosList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PhotoItemBinding.inflate(inflater, parent, false)
        return PhotosViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        holder.bindItem(photosList[position])
    }

    override fun getItemCount(): Int {
        return photosList.size
    }
}

class PhotosViewHolder(
    private val binding: PhotoItemBinding,
    private val onClick: (Photo) -> Unit
): RecyclerView.ViewHolder(binding.root) {

    private lateinit var item: Photo

    init {
        itemView.setOnClickListener {
            onClick(item)
        }
    }

    fun bindItem(photos: Photo) = with(binding) {
        item = photos
        val photoLink = photos.photoLink
        Glide.with(ivPhotoItem)
            .load(photoLink)
            .into(ivPhotoItem)
        tvSearchResult.text = photoLink
    }
}
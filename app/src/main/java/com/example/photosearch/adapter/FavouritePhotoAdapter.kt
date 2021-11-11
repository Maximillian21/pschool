package com.example.photosearch.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photosearch.data.Photo
import com.example.photosearch.databinding.FavouriteItemBinding
import com.example.photosearch.viewmodels.FavoritesViewModel

class FavouritePhotoAdapter(
    private val viewModel: FavoritesViewModel,
    private val onClick: (Photo) -> Unit
): RecyclerView.Adapter<FavouritePhotosViewHolder>() {

    private  var photosList: MutableList<Photo> = mutableListOf()

    fun addData(newPhoto: Photo, position: Int) {
        photosList.add(newPhoto)
        notifyItemInserted(position)
    }

    fun removeItem(position: Int) {
        viewModel.removePhoto(photosList[position])
        photosList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritePhotosViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FavouriteItemBinding.inflate(inflater, parent, false)
        return FavouritePhotosViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: FavouritePhotosViewHolder, position: Int) {
        holder.bindItem(photosList[position])
        holder.binding.btnRemove.setOnClickListener {
            viewModel.removePhoto(photosList[position])
        }
    }

    override fun getItemCount(): Int {
        return photosList.size
    }
}

class FavouritePhotosViewHolder(
    val binding: FavouriteItemBinding,
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
        val photoLink = "https://live.staticflickr.com/${photos.server}/${photos.id}_${photos.secret}_m.jpg"
        Glide.with(ivPhotoItem)
            .load(photoLink)
            .into(ivPhotoItem)
    }
}
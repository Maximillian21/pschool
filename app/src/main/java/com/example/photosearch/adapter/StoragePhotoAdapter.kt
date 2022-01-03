package com.example.photosearch.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.photosearch.data.StoragePhoto
import com.example.photosearch.databinding.StoragePhotoItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoragePhotoAdapter(val context: Context, val onPhotoSwiped: (StoragePhoto) -> Unit) : RecyclerView.Adapter<StoragePhotoViewHolder>(){
    private  var photosList: MutableList<StoragePhoto> = mutableListOf()

    fun addData(newPhotosList: List<StoragePhoto>) {
        photosList.addAll(newPhotosList)
        notifyDataSetChanged()
    }

    fun insertPhoto(photo: StoragePhoto) {
        photosList.add(photo)
        notifyItemInserted(photosList.lastIndex)
    }

    fun deletePhoto(position: Int) {
        val photo = photosList[position]
        onPhotoSwiped(photo)
        photosList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoragePhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = StoragePhotoItemBinding.inflate(inflater, parent, false)
        return StoragePhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoragePhotoViewHolder, position: Int) {
        holder.bindItem(photosList[position])
    }

    override fun getItemCount(): Int {
        return photosList.size
    }
}

class StoragePhotoViewHolder(
    val binding: StoragePhotoItemBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bindItem(photo: StoragePhoto) {
        Log.d("StorageAdapter", photo.uri.toString())
        Glide.with(binding.ivStoragePhoto)
            .load(photo.uri)
            .into(binding.ivStoragePhoto)
    }

}
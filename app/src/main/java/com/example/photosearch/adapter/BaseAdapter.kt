package com.example.photosearch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.photosearch.data.Photo

abstract class BaseAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    protected var photosList = mutableListOf<Photo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return getViewHolder(LayoutInflater.from(parent.context)
                .inflate(viewType, parent, false), viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    override fun getItemCount(): Int = photosList.size

    override fun getItemViewType(position: Int): Int = getLayoutId(position, photosList[position])

    open fun removeItem(position: Int) {
        photosList.removeAt(position)
        notifyItemRemoved(position)
    }

    open fun setData(newPhotosList: MutableList<Photo>) {
        photosList = newPhotosList
        notifyDataSetChanged()
    }

    protected abstract fun getLayoutId(position: Int, obj: Photo): Int

    protected abstract fun getViewHolder(view: View, viewType: Int): RecyclerView.ViewHolder

}
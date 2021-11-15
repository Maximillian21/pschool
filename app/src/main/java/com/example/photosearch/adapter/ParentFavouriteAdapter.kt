package com.example.photosearch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.photosearch.data.Photo
import com.example.photosearch.databinding.ParentRecyclerItemBinding
import com.example.photosearch.viewmodels.FavoritesViewModel

class ParentFavouriteAdapter(
    private val viewModel: FavoritesViewModel,
    private val onClick: (Photo) -> Unit
): RecyclerView.Adapter<ParentFavouriteViewHolder>() {

    private  var photosList: MutableList<Photo> = mutableListOf()

    fun setData(newPhotosList :MutableList<Photo>) {
        photosList.clear()
        newPhotosList.sortBy { it.searchText }
        photosList.addAll(newPhotosList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentFavouriteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ParentRecyclerItemBinding.inflate(inflater, parent, false)
        return ParentFavouriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ParentFavouriteViewHolder, position: Int) {
        val adapter = FavouritePhotoAdapter(viewModel, onClick)
        adapter.addData(photosList[position], position)

        if (position > 0 && photosList[position - 1].searchText.substring(0, 1) ==
            photosList[position].searchText.substring(0, 1)) {
            holder.binding.section.visibility = View.GONE
        }
        else {
            holder.binding.section.text = photosList[position].searchText
        }
        holder.binding.rvChild.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(SwipeFavoritesDelete(adapter))
        itemTouchHelper.attachToRecyclerView(holder.binding.rvChild)
    }

    override fun getItemCount(): Int {
        return photosList.size
    }
}

class ParentFavouriteViewHolder(
    val binding: ParentRecyclerItemBinding
): RecyclerView.ViewHolder(binding.root)
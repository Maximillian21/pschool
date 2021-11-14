package com.example.photosearch.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.photosearch.R
import com.example.photosearch.databinding.FragmentFullViewBinding
import com.example.photosearch.viewmodels.FullViewViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FullViewFragment: Fragment() {
    private var _binding: FragmentFullViewBinding? = null
    private val binding: FragmentFullViewBinding
        get() = _binding!!

    private val viewModel: FullViewViewModel by viewModels()
    private val args: FullViewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFullViewBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("FullViewFragment", args.photo.searchText)
        binding.tvPhotoTitle.text = args.photo.searchText

        val photoLink = "https://live.staticflickr.com/${args.photo.server}/${args.photo.id}_${args.photo.secret}.jpg"
        Glide.with(binding.ivPhotoPreview)
            .load(photoLink)
            .into(binding.ivPhotoPreview)

        val isExists = viewModel.isExists(args.photo.id)
        isExists.observe(viewLifecycleOwner) { isExist ->
            if(isExist) {
                binding.btnFavourites.setText(R.string.remove_favourites)
            }
            else {
                binding.btnFavourites.setText(R.string.save_favourites)
            }
        }

        binding.btnFavourites.setOnClickListener {
            if(isExists.value == true) {
                viewModel.delete(args.photo)
            }
            else {
                viewModel.save(args.photo)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
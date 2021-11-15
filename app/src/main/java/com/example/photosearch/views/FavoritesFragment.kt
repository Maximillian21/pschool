package com.example.photosearch.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.photosearch.adapter.*
import com.example.photosearch.databinding.FragmentFavoritesBinding
import com.example.photosearch.viewmodels.FavoritesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment: Fragment() {
    private var _binding: FragmentFavoritesBinding? = null
    private val binding: FragmentFavoritesBinding
        get() = _binding!!

    private val args: FavoritesFragmentArgs by navArgs()
    private val viewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getFavoritesPhotos(args.account.id)
        observeViewModel()
    }

    private fun observeViewModel() {
        val adapter = ParentFavouriteAdapter(viewModel) { photo ->
            findNavController().navigate(MainFragmentDirections.showFullView(photo))
        }
        viewModel.savedPhoto.observe(viewLifecycleOwner) {
            adapter.setData(it.toMutableList())
        }
        binding.rvPhotos.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
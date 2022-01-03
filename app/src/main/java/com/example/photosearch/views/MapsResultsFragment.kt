package com.example.photosearch.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.photosearch.adapter.PhotosChildAdapter
import com.example.photosearch.databinding.FragmentMapsResultsBinding
import com.example.photosearch.viewmodels.MapsResultsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapsResultsFragment: Fragment() {
    private var _binding: FragmentMapsResultsBinding? = null
    private val binding: FragmentMapsResultsBinding
        get() = _binding!!

    private val args: MapsResultsFragmentArgs by navArgs()

    private val viewModel: MapsResultsViewModel by viewModels()

    private val dataAdapter = PhotosChildAdapter { photo ->
        findNavController().navigate(MainFragmentDirections.showFullView(photo))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsResultsBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvResults.adapter = dataAdapter
        observeViewModel()
        viewModel.refresh(args.coordinates.latitude, args.coordinates.longitude)
    }

    private fun observeViewModel() {
        viewModel.photosList.observe(viewLifecycleOwner, { results ->
            results?.let {
                GlobalScope.launch(Dispatchers.IO) {
                    val coordinates = args.coordinates.latitude.toString() + ", " + args.coordinates.longitude.toString()
                    viewModel.setPhotoValues(it, coordinates)
                }
                dataAdapter.setData(it.photos.photo.toMutableList())
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, { isLoading ->
            isLoading?.let {
                binding.loadingView.visibility = if(it) View.VISIBLE else View.GONE
                binding.rvResults.visibility = if(it) View.GONE else View.VISIBLE
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
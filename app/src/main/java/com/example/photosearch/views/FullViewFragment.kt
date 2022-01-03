package com.example.photosearch.views

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.photosearch.R
import com.example.photosearch.databinding.FragmentFullViewBinding
import com.example.photosearch.viewmodels.FullViewViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*


@AndroidEntryPoint
class FullViewFragment: Fragment() {
    private var _binding: FragmentFullViewBinding? = null
    private val binding: FragmentFullViewBinding
        get() = _binding!!
    
    private val PERMISSION_WRITE = 0

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

        val photoLink = args.photo.photoLink
        Glide.with(binding.ivPhotoPreview)
            .load(photoLink)
            .into(binding.ivPhotoPreview)

        val isExists = viewModel.isExists(args.photo.id)
        isExists.observe(viewLifecycleOwner) { isExist ->
            if(isExist) {
                binding.btnFavourites.setText(R.string.remove_favorites)
            }
            else {
                binding.btnFavourites.setText(R.string.save_favorites)
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

        binding.btnSaveOnDevise.setOnClickListener {
            getStoragePermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        if (requestCode == PERMISSION_WRITE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("FullViewFragment", "Permission was granted")
                writeImage()
            } else {
                Log.d("FullViewFragment", "Permission denied")
            }
        }
    }

    private fun getStoragePermission() {
        if(ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) {
            writeImage()
        } else {
            requestStoragePermission()
        }
    }

    private fun writeImage() {
        val bitmap = binding.ivPhotoPreview.drawable.toBitmap()
        lifecycleScope.launch {
            viewModel.savePhotoToExternalStorage(UUID.randomUUID().toString(),
                bitmap, requireContext())
        }

    }

    private fun requestStoragePermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            val snackbar = Snackbar.make(binding.layoutFullView,
                getString(R.string.external_permission),
                Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction(getString(R.string.ok)) {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_WRITE)
            }.show()

        } else {
            Log.d("FullViewFragment", "Storage permission is not granted")
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_WRITE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
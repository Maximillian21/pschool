package com.example.photosearch.views

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.photosearch.R
import com.example.photosearch.adapter.StoragePhotoAdapter
import com.example.photosearch.adapter.SwipeStorageDelete
import com.example.photosearch.data.StoragePhoto
import com.example.photosearch.databinding.FragmentGalleryBinding
import com.example.photosearch.viewmodels.GalleryViewModel
import com.google.android.material.snackbar.Snackbar
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class GalleryFragment: Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding: FragmentGalleryBinding
        get() = _binding!!

    private val PERMISSION_WRITE = 0

    private val viewModel: GalleryViewModel by viewModels()

    private lateinit var storagePhotoAdapter: StoragePhotoAdapter
    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>

    private var deletedImageUri: Uri? = null

    val externalPath = "content://media/external/images/media"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storagePhotoAdapter = StoragePhotoAdapter(requireContext()) {
            lifecycleScope.launch {
                if(it.uri.toString().contains(externalPath)) {
                    viewModel.deletePhotoFromExternalStorage(it.uri, requireActivity(), intentSenderLauncher)
                    deletedImageUri = it.uri
                }
                else {
                    viewModel.deletePhotoFromInternalStorage(it.uri)
                }
            }
        }

        createIntentSender()

        binding.rvStoragePhotos.adapter = storagePhotoAdapter
        attachItemTouchHelper()

        lifecycleScope.launch {
            storagePhotoAdapter.addData(viewModel.loadInternalStorage(requireContext()))
        }

        getStoragePermission()

        val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            lifecycleScope.launch {
                savePhotoToInternalStorage("${UUID.randomUUID()}.jpg", it)
            }
        }

        binding.btnTakePhoto.setOnClickListener {
            takePhoto.launch()
        }
    }

    private fun createIntentSender() {
        intentSenderLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if(it.resultCode == RESULT_OK) {
                if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    lifecycleScope.launch {
                        viewModel.deletePhotoFromExternalStorage(deletedImageUri ?: return@launch, requireActivity(), intentSenderLauncher)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Photo can't be deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun attachItemTouchHelper() {
        val itemTouchHelper = ItemTouchHelper(SwipeStorageDelete(storagePhotoAdapter))
        itemTouchHelper.attachToRecyclerView(binding.rvStoragePhotos)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            storagePhotoAdapter.insertPhoto(StoragePhoto ("CroppedFile", resultUri!!))
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Log.d("GalleryFragment", cropError.toString())
            Toast.makeText(requireContext(), "Error loading edited file", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private suspend fun savePhotoToInternalStorage(filename: String, bmp: Bitmap) {
        withContext(Dispatchers.IO) {
            try {
                val wrapper = ContextWrapper(requireContext())
                var file = wrapper.getDir("images", Context.MODE_PRIVATE)
                file = File(file, filename)
                val stream: OutputStream = FileOutputStream(file)
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()

                storagePhotoAdapter.insertPhoto(StoragePhoto(filename, Uri.fromFile(file)))

                val snackbar = Snackbar.make(binding.galleryLayout,
                    getString(R.string.edit_question),
                    Snackbar.LENGTH_LONG)
                snackbar.setAction(getString(R.string.yes)) {
                    startCrop(Uri.fromFile(file))
                }.show()
            } catch(e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun startCrop(uri: Uri) {
        val newFileName = "${UUID.randomUUID()}CROP.jpg"
        val wrapper = ContextWrapper(requireContext())
        var file = wrapper.getDir("images", Context.MODE_PRIVATE)
        file = File(file, newFileName)
        val uCrop = UCrop.of(uri, Uri.fromFile(file))
            .withOptions(getCropOptions())

        uCrop.start(requireContext(), this)
    }

    private fun getCropOptions(): UCrop.Options {
        val options = UCrop.Options()
        options.setCompressionQuality(100)
        return options
    }

    private fun getStoragePermission() {
        if(ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED) {
            loadExternalImages()
        } else {
            requestStoragePermission()
        }
    }

    private fun loadExternalImages() {
        lifecycleScope.launch {
            storagePhotoAdapter.addData(viewModel.loadFromExternalStorage(requireContext()))
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        if (requestCode == PERMISSION_WRITE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("FullViewFragment", "Permission was granted")
                loadExternalImages()
            } else {
                Log.d("FullViewFragment", "Permission denied")
            }
        }
    }

    private fun requestStoragePermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
            shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            val snackbar = Snackbar.make(binding.galleryLayout,
                getString(R.string.external_permission),
                Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction(getString(R.string.ok)) {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_WRITE)
            }.show()
        } else {
            Log.d("GalleryFragment", "Storage permission is not granted")
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_WRITE)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
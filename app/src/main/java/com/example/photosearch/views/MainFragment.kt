package com.example.photosearch.views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.photosearch.BaseApp
import com.example.photosearch.R
import com.example.photosearch.adapter.PhotosChildAdapter
import com.example.photosearch.adapter.SwipeDelete
import com.example.photosearch.databinding.FragmentMainBinding
import com.example.photosearch.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment: Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding
        get() = _binding!!

    private val history = mutableListOf<String>()
    private lateinit var dropDownAdapter: ArrayAdapter<String>
    private val viewModel: MainViewModel by viewModels()

    private val dataAdapter = PhotosChildAdapter { photo ->
        findNavController().navigate(MainFragmentDirections.showFullView(photo))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchField.setText(viewModel.loadPref(requireActivity()))
        setSearchHistory()

        setListeners()
        observeViewModel()

        binding.rvPhotos.adapter = dataAdapter
        binding.searchField.setAdapter(dropDownAdapter)
        attachItemTouchHelper()

        binding.rvPhotos.addOnScrollListener(viewModel.getOnScroll(binding.rvPhotos.layoutManager!!,
            binding.searchField.text.toString()))

        Log.d("MainFragment", BaseApp.globalAccountId.toString())
    }

    private fun setSearchHistory() {
        dropDownAdapter = ArrayAdapter(requireContext(),
            android.R.layout.simple_dropdown_item_1line, history)
        viewModel.getHistory(BaseApp.globalAccountId)
    }

    private fun setListeners() {
        binding.findButton.setOnClickListener {
            val text = binding.searchField.text.toString()
            if(text.isNotBlank()) {
                viewModel.addQuery(text)
                viewModel.refresh(text)
            }
        }

        binding.searchField.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                binding.searchField.showDropDown()
            }
        }
    }

    private fun attachItemTouchHelper() {
        val itemTouchHelper = ItemTouchHelper(SwipeDelete(dataAdapter))
        itemTouchHelper.attachToRecyclerView(binding.rvPhotos)
    }

    private fun observeViewModel() {
        viewModel.photosList.observe(viewLifecycleOwner, { results ->
            results?.let {
                GlobalScope.launch(Dispatchers.IO) {
                    viewModel.setPhotoValues(it, binding.searchField.text.toString())
                }
                //if pages count was changed it will update
                viewModel.pages = it.photos.pages
                dataAdapter.setData(it.photos.photo.toMutableList())
            }
        })

        viewModel.loading.observe(viewLifecycleOwner, { isLoading ->
            isLoading?.let {
                binding.loadingView.visibility = if(it) View.VISIBLE else View.GONE
                binding.rvPhotos.visibility = if(it) View.GONE else View.VISIBLE
            }
        })

        viewModel.searchHistory.observe(viewLifecycleOwner, { historyList ->
            history.clear()
            history.addAll(historyList.map { it.query })
            dropDownAdapter.notifyDataSetChanged()
        })
    }

    override fun onStop() {
        super.onStop()
        viewModel.savePref(binding.searchField.text.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
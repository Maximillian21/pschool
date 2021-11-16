package com.example.photosearch.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.photosearch.data.Account
import com.example.photosearch.databinding.FragmentLoginBinding
import com.example.photosearch.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment: Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding
        get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSingIn.setOnClickListener {
            if(binding.etLogin.text.toString().isNotBlank()) {
                val isExists = viewModel.isExists(binding.etLogin.text.toString())
                isExists.observe(viewLifecycleOwner) { isExist ->
                    if(isExist) {
                        val existingAccount = viewModel.setAccountId(binding.etLogin.text.toString())
                        existingAccount.observe(viewLifecycleOwner) {
                            findNavController().navigate(LoginFragmentDirections.enterAccount(it))
                        }
                    }
                    else {
                        GlobalScope.launch(Dispatchers.Main) {
                            viewModel.addAccount(Account(binding.etLogin.text.toString()))
                            viewModel.account.observe(viewLifecycleOwner) {
                                findNavController().navigate(LoginFragmentDirections.enterAccount(it))
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
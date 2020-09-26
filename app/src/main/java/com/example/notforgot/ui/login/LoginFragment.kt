package com.example.notforgot.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notforgot.databinding.FragmentLoginBinding
import com.example.notforgot.model.ResultWrapper
import com.example.notforgot.util.showShortText
import timber.log.Timber

class LoginFragment : Fragment() {
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            if (checkInputs())
                viewModel.login(binding.mail.text.toString(), binding.password.text.toString())
                    .observe(viewLifecycleOwner) {
                        when (it) {
                            is ResultWrapper.Loading -> Timber.i("Loading")
                            is ResultWrapper.Error -> Timber.i("Error")
                            is ResultWrapper.Success -> {
                                viewModel.completeLogin(it.value)
                                viewModel.navigateToMain()
                            }
                        }
                    }
        }

        binding.buttonRegister.setOnClickListener {
            viewModel.navigateToRegister()
        }

        viewModel.navigateRegister.observe(viewLifecycleOwner) {
            it?.let {
                if (it) findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                )
            }
            viewModel.navigateToRegisterDone()
        }

        viewModel.navigateMain.observe(viewLifecycleOwner) {
            it?.let {
                if (it) findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToMainFragment()
                )
            }
            viewModel.navigateToMainDone()
        }
    }

    private fun checkInputs(): Boolean {
        var returnVal = true

        if (binding.mail.text.isNullOrEmpty()) {
            requireContext().showShortText("Mail cannot be empty!")
            returnVal = false
        }
        if (binding.password.text.isNullOrEmpty()) {
            requireContext().showShortText("Password cannot be empty!")
            returnVal = false
        }

        return returnVal
    }

}
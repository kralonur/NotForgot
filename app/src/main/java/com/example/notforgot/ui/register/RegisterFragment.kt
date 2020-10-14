package com.example.notforgot.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notforgot.R
import com.example.notforgot.databinding.FragmentRegisterBinding
import com.example.notforgot.model.domain.ResultWrapper
import com.example.notforgot.util.invalidateError
import com.example.notforgot.util.showShortText
import timber.log.Timber

class RegisterFragment : Fragment(), RegisterValidation {
    private val viewModel by viewModels<RegisterViewModel>()
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        invalidateInput()

        binding.buttonLogin.setOnClickListener {
            navigateToLogin()
        }

        binding.buttonRegister.setOnClickListener {
            tryRegister()
        }

        registerResponse()

        fetchResponse()
    }

    override fun validateEmail(validationMessage: String) {
        binding.textFieldMail.error = validationMessage
    }

    override fun validatePassword(validationMessage: String) {
        binding.textFieldPass.error = validationMessage
    }

    override fun validateName(validationMessage: String) {
        binding.textFieldName.error = validationMessage
    }

    private fun navigateToLogin() {
        findNavController().navigate(
            RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
        )
    }

    private fun navigateToMain() {
        findNavController().navigate(
            RegisterFragmentDirections.actionRegisterFragmentToMainActivity2()
        )
        requireActivity().finish()
    }

    private fun registerResponse() {
        viewModel.registerResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> Timber.i("Loading")
                is ResultWrapper.NetworkError -> requireContext().showShortText(getString(R.string.error_network_connection))
                is ResultWrapper.ServerError -> with(requireContext()) {
                    if (it.code == 400) this.showShortText(getString(R.string.error_user_exist))
                    else this.showShortText(getString(R.string.error_server_error))
                }
                is ResultWrapper.Error -> requireContext().showShortText(getString(R.string.register_unsuccessful))
                is ResultWrapper.Success -> Timber.i("Register successful")
            }
        }
    }

    private fun fetchResponse() {
        viewModel.fetchResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> requireContext().showShortText(getString(R.string.data_downloading_from_cloud))
                is ResultWrapper.NetworkError -> requireContext().showShortText(getString(R.string.error_network_connection))
                is ResultWrapper.Success -> {
                    requireContext().showShortText(getString(R.string.data_successfully_saved_to_db))
                    navigateToMain()
                }
                else -> {
                    requireContext().showShortText(getString(R.string.fetching_data_unsuccessful))
                }
            }
        }
    }

    private fun invalidateInput() {
        binding.name.invalidateError(binding.textFieldName)
        binding.mail.invalidateError(binding.textFieldMail)
        binding.password.invalidateError(binding.textFieldPass)
        binding.passwordRepeat.invalidateError(binding.textFieldPassRepeat)
    }

    private fun tryRegister() {
        val mail = binding.mail.text.toString()
        val name = binding.name.text.toString()
        val pass = binding.password.text.toString()
        val passAgain = binding.passwordRepeat.text.toString()

        viewModel.tryRegister(mail, name, pass, passAgain, this)
    }

}
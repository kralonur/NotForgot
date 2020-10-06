package com.example.notforgot.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notforgot.R
import com.example.notforgot.databinding.FragmentLoginBinding
import com.example.notforgot.model.domain.ResultWrapper
import com.example.notforgot.model.remote.authentication.login.LoginResponse
import com.example.notforgot.util.isMail
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        invalidateInput()

        binding.buttonLogin.setOnClickListener {
            if (checkInputs())
                tryLogin()
        }

        binding.buttonRegister.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun navigateToRegister() {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
        )
    }

    private fun navigateToMain() {
        findNavController().navigate(
            LoginFragmentDirections.actionLoginFragmentToMainActivity2()
        )
        requireActivity().finish()
    }

    private fun checkInputs(): Boolean {
        var returnVal = true

        if (binding.mail.text.isNullOrEmpty()) {
            binding.textFieldMail.error = getString(R.string.mail_cannot_be_empty)
            returnVal = false
        } else if (!binding.mail.text.toString().isMail()) {
            binding.textFieldMail.error = getString(R.string.mail_is_not_valid)
            returnVal = false
        }

        if (binding.password.text.isNullOrEmpty()) {
            binding.textFieldPass.error = getString(R.string.password_cannot_be_empty)
            returnVal = false
        }

        return returnVal
    }

    private fun invalidateInput() {
        binding.mail.doAfterTextChanged {
            if (binding.textFieldMail.error != null)
                binding.textFieldMail.error = null
        }

        binding.password.doAfterTextChanged {
            if (binding.textFieldPass.error != null)
                binding.textFieldPass.error = null
        }
    }

    private fun tryLogin() {
        viewModel.login(binding.mail.text.toString(), binding.password.text.toString())
            .observe(viewLifecycleOwner) {
                when (it) {
                    is ResultWrapper.Loading -> Timber.i("Loading")
                    is ResultWrapper.NetworkError -> requireContext().showShortText(getString(R.string.error_network_connection))
                    is ResultWrapper.ServerError -> with(requireContext()) {
                        if (it.code == 404) this.showShortText(getString(R.string.error_user_does_not_exist))
                        else this.showShortText(getString(R.string.error_server_error))
                    }
                    is ResultWrapper.Error -> requireContext().showShortText(getString(R.string.login_unsuccessful))
                    is ResultWrapper.Success -> doLoginSuccess(it.value)
                }
            }
    }

    private fun doLoginSuccess(loginResponse: LoginResponse) {
        viewModel.completeLogin(loginResponse)

        viewModel.fetchFromCloud().observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> requireContext().showShortText(getString(R.string.data_downloading_from_cloud))
                is ResultWrapper.NetworkError -> requireContext().showShortText(getString(R.string.error_network_connection))
                is ResultWrapper.Success -> {
                    requireContext().showShortText(getString(R.string.data_successfully_saved_to_db))
                    navigateToMain()
                }
                else -> {
                    requireContext().showShortText(getString(R.string.fetching_data_unsuccessful))
                    viewModel.loginUnsuccessful()
                }
            }
        }
    }

}


package com.example.notforgot.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notforgot.R
import com.example.notforgot.databinding.FragmentRegisterBinding
import com.example.notforgot.model.ResultWrapper
import com.example.notforgot.model.authentication.register.RegisterResponse
import com.example.notforgot.util.isMail
import com.example.notforgot.util.showShortText
import timber.log.Timber

class RegisterFragment : Fragment() {
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
            viewModel.navigateToLogin()
        }

        binding.buttonRegister.setOnClickListener {
            if (checkInputs())
                tryRegister()
        }

        viewModel.navigateLogin.observe(viewLifecycleOwner) {
            it?.let {
                if (it) findNavController().navigate(
                    RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                )
            }
            viewModel.navigateToLoginDone()
        }

        viewModel.navigateMain.observe(viewLifecycleOwner) {
            it?.let {
                if (it) findNavController().navigate(
                    RegisterFragmentDirections.actionRegisterFragmentToMainFragment()
                )
            }
            viewModel.navigateToMainDone()
        }
    }

    private fun checkInputs(): Boolean {
        var returnVal = true

        if (binding.name.text.isNullOrEmpty()) {
            binding.textFieldName.error = getString(R.string.name_cannot_be_empty)
            returnVal = false
        }

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
        } else {
            if (binding.passwordRepeat.text.toString() != binding.password.text.toString()) {
                binding.textFieldPassRepeat.error = getString(R.string.passwords_should_be_same)
                returnVal = false
            }
        }

        return returnVal
    }

    private fun invalidateInput() {
        binding.name.doAfterTextChanged {
            if (binding.textFieldName.error != null)
                binding.textFieldName.error = null
        }

        binding.mail.doAfterTextChanged {
            if (binding.textFieldMail.error != null)
                binding.textFieldMail.error = null
        }

        binding.password.doAfterTextChanged {
            if (binding.textFieldPass.error != null)
                binding.textFieldPass.error = null
        }

        binding.passwordRepeat.doAfterTextChanged {
            if (binding.textFieldPassRepeat.error != null)
                binding.textFieldPassRepeat.error = null
        }
    }

    private fun tryRegister() {
        viewModel.register(binding.mail.text.toString(),
            binding.name.text.toString(),
            binding.password.text.toString())
            .observe(viewLifecycleOwner) {
                when (it) {
                    is ResultWrapper.Loading -> Timber.i("Loading")
                    is ResultWrapper.NetworkError -> requireContext().showShortText(getString(R.string.error_network_connection))
                    is ResultWrapper.ServerError -> with(requireContext()) {
                        if (it.code == 400) this.showShortText(getString(R.string.error_user_exist))
                        else this.showShortText(getString(R.string.error_server_error))
                    }
                    is ResultWrapper.Error -> requireContext().showShortText(getString(R.string.register_unsuccessful))
                    is ResultWrapper.Success -> doRegisterSuccess(it.value)
                }
            }
    }

    private fun doRegisterSuccess(registerResponse: RegisterResponse) {
        viewModel.completeRegister(registerResponse)

        viewModel.fetchFromCloud().observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> requireContext().showShortText(getString(R.string.data_downloading_from_cloud))
                is ResultWrapper.NetworkError -> requireContext().showShortText(getString(R.string.error_network_connection))
                is ResultWrapper.Success -> {
                    requireContext().showShortText(getString(R.string.data_successfully_saved_to_db))
                    viewModel.navigateToMain()
                }
                else -> {
                    requireContext().showShortText(getString(R.string.fetching_data_unsuccessful))
                    viewModel.registerUnsuccessful()
                }
            }
        }
    }

}
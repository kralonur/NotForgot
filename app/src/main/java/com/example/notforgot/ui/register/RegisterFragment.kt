package com.example.notforgot.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
        binding.lifecycleOwner = viewLifecycleOwner
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
            binding.textFieldName.error = "Name cannot be empty!"
            returnVal = false
        }

        if (binding.mail.text.isNullOrEmpty()) {
            binding.textFieldMail.error = "Mail cannot be empty!"
            returnVal = false
        } else if (!binding.mail.text.toString().isMail()) {
            binding.textFieldMail.error = "Mail is not valid!"
            returnVal = false
        }

        if (binding.password.text.isNullOrEmpty()) {
            binding.textFieldPass.error = "Password cannot be empty!"
            returnVal = false
        } else {
            if (binding.passwordRepeat.text.toString() != binding.password.text.toString()) {
                binding.textFieldPassRepeat.error = "Passwords should be same!"
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
                    is ResultWrapper.Error -> requireContext().showShortText("Register unsuccessful!")
                    is ResultWrapper.Success -> doRegisterSuccess(it.value)
                }
            }
    }

    private fun doRegisterSuccess(registerResponse: RegisterResponse) {
        viewModel.completeRegister(registerResponse)

        viewModel.fetchFromCloud().observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> Timber.i("Data downloading from cloud")
                is ResultWrapper.Error -> {
                    requireContext().showShortText("Fetching data unsuccessful!")
                    viewModel.registerUnsuccessful()
                }
                is ResultWrapper.Success -> {
                    requireContext().showShortText("Data successfully saved to db!")
                    viewModel.navigateToMain()
                }
            }
        }
    }

}
package com.example.notforgot.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notforgot.databinding.FragmentRegisterBinding
import com.example.notforgot.util.showShortText

class RegisterFragment : Fragment() {
    private val viewModel by viewModels<RegisterViewModel>()
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            viewModel.navigateToLogin()
        }

        binding.buttonRegister.setOnClickListener {
            checkInputs()
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

    private fun checkInputs() {
        if (binding.name.text.isNullOrEmpty()) {
            requireContext().showShortText("Name cannot be empty!")
        }

        if (binding.mail.text.isNullOrEmpty()) {
            requireContext().showShortText("Mail cannot be empty!")
        }

        if (binding.password.text.isNullOrEmpty()) {
            requireContext().showShortText("Password cannot be empty!")
        } else {
            if (binding.passwordRepeat.text.toString() != binding.password.text.toString()) {
                requireContext().showShortText("Passwords should be same!")
            }
        }
    }

}
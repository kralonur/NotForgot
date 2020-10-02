package com.example.notforgot.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.notforgot.databinding.FragmentSplashBinding
import com.example.notforgot.util.getToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch {
            delay(2000)

            val token = requireContext().getToken()
            if (token.isEmpty() || token.isBlank()) {
                navigateToLogin()
            } else {
                navigateToMain()
            }
        }

    }

    private fun navigateToLogin() {
        findNavController().navigate(
            SplashFragmentDirections.actionSplashFragmentToLoginFragment()
        )
    }

    private fun navigateToMain() {
        findNavController().navigate(
            SplashFragmentDirections.actionSplashFragmentToMainFragment()
        )
    }
}
package com.example.notforgot.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notforgot.databinding.FragmentSplashBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private val viewModel by viewModels<SplashViewModel>()
    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        GlobalScope.launch {
            delay(2000)

            viewModel.navigateToLogin()
        }

        viewModel.navigateLogin.observe(viewLifecycleOwner) {
            it?.let {
                if (it) findNavController().navigate(
                    SplashFragmentDirections.actionSplashFragmentToLoginFragment()
                )
            }
            viewModel.navigateToLoginDone()
        }
    }
}
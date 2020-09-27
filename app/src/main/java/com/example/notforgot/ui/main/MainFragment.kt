package com.example.notforgot.ui.main

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.notforgot.R
import com.example.notforgot.databinding.FragmentMainBinding
import com.example.notforgot.databinding.LayoutLottieBinding
import com.example.notforgot.model.ResultWrapper
import com.example.notforgot.model.db.items.DbTask
import com.example.notforgot.recview.TaskAdapter
import com.example.notforgot.recview.TaskClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class MainFragment : Fragment(), TaskClickListener {
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TaskAdapter(this)
        binding.recView.adapter = adapter

        viewModel.getTaskList().observe(viewLifecycleOwner) {
            Timber.i(it.toString())
            adapter.submitList(it)
        }

        binding.addButton.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToTaskCreateFragment()
            )
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            val lottieBinding = LayoutLottieBinding.inflate(layoutInflater)
            val dialog = lottieDialog(lottieBinding)

            viewModel.uploadToCloud().observe(viewLifecycleOwner) {
                when (it) {
                    is ResultWrapper.Loading -> {
                        Timber.i("Uploading")
                        dialog.setMessage("Uploading")
                        lottieBinding.text.text = "Uploading"
                        setLoadingAnimation()
                    }
                    is ResultWrapper.Error -> {
                        Timber.e("Upload unsuccessful!")
                        dialog.setMessage("Upload unsuccessful!")
                        lottieBinding.text.text = "Upload unsuccessful!"
                        binding.swipeRefreshLayout.isRefreshing = false
                        setErrorAnimation(lottieBinding.animationView, dialog)
                    }
                    is ResultWrapper.Success -> {
                        Timber.i("Upload successful!")
                        dialog.setMessage("Upload successful!")
                        lottieBinding.text.text = "Upload successful!"
                        binding.swipeRefreshLayout.isRefreshing = false
                        setSuccessAnimation(lottieBinding.animationView, dialog)
                    }
                }
            }
        }

        viewModel.navigateDetail.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToTaskDetailFragment(it.id)
                )
            }
            viewModel.navigateToDetailDone()
        }
    }

    private fun setLoadingAnimation(animation: LottieAnimationView) {
        animation.setAnimation(R.raw.success)
    }

    private fun setErrorAnimation(animation: LottieAnimationView, dialog: AlertDialog) {
        animation.cancelAnimation()
        animation.setAnimation(R.raw.fail)
        animation.repeatCount = 1
        animation.playAnimation()
        animation.removeAllAnimatorListeners()

        animation.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                dialog.dismiss()
            }

            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
        })
    }

    private fun setSuccessAnimation(animation: LottieAnimationView, dialog: AlertDialog) {
        animation.cancelAnimation()
        animation.setAnimation(R.raw.success)
        animation.playAnimation()

        GlobalScope.launch {
            delay(3000)
            dialog.dismiss()
        }
    }

    override fun onClick(task_data: DbTask) {
        viewModel.navigateToDetail(task_data)
    }

    private fun lottieDialog(lottieBinding: LayoutLottieBinding): AlertDialog {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(lottieBinding.root)
            .show()

        return dialog
    }
}
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.example.notforgot.R
import com.example.notforgot.databinding.DialogLoadingBinding
import com.example.notforgot.databinding.FragmentMainBinding
import com.example.notforgot.model.db.items.DbTask
import com.example.notforgot.model.domain.ResultWrapper
import com.example.notforgot.ui.main.recview.TaskAdapter
import com.example.notforgot.ui.main.recview.TaskClickListener
import com.example.notforgot.util.showShortText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TaskAdapter(this)
        binding.recView.adapter = adapter


        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.currentList[viewHolder.adapterPosition].task!!
                Timber.i(deletedItem.toString())

                val snackbar =
                    Snackbar.make(view, getString(R.string.undo_delete_q), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.undo)) {
                            adapter.notifyDataSetChanged()
                        }

                snackbar.addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        if (event != DISMISS_EVENT_ACTION)
                            viewModel.deleteTask(deletedItem.task).observe(viewLifecycleOwner) {
                                when (it) {
                                    is ResultWrapper.Success -> requireContext().showShortText(
                                        getString(R.string.s_deleted, deletedItem.task.title)
                                    )
                                }
                            }
                    }
                })

                snackbar.show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(binding.recView)

        viewModel.getRecviewItemList().observe(viewLifecycleOwner) {
            Timber.i(it.toString())
            if (it.isEmpty()) hideList() else showList()
            adapter.submitList(it)
        }

        binding.addButton.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToTaskCreateFragment()
            )
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            val lottieBinding = DialogLoadingBinding.inflate(layoutInflater)
            val dialog = lottieDialog(lottieBinding)

            viewModel.uploadToCloud().observe(viewLifecycleOwner) {
                when (it) {
                    is ResultWrapper.Loading -> {
                        lottieBinding.text.text = getString(R.string.uploading)
                        setLoadingAnimation(lottieBinding.animationView)
                    }
                    is ResultWrapper.Success -> {
                        lottieBinding.text.text = getString(R.string.upload_successful)
                        binding.swipeRefreshLayout.isRefreshing = false
                        setSuccessAnimation(lottieBinding.animationView, dialog)
                    }
                    else -> {
                        when (it) {
                            is ResultWrapper.NetworkError -> lottieBinding.text.text =
                                getString(R.string.error_network_connection)
                            is ResultWrapper.ServerError -> lottieBinding.text.text =
                                getString(R.string.error_server_error)
                            else -> lottieBinding.text.text =
                                getString(R.string.upload_unsuccessful)

                        }
                        binding.swipeRefreshLayout.isRefreshing = false
                        setErrorAnimation(lottieBinding.animationView, dialog)
                    }
                }
            }
        }
    }

    private fun navigateToDetail(id: Int) {
        findNavController().navigate(
            MainFragmentDirections.actionMainFragmentToTaskDetailFragment(id)
        )
    }

    private fun hideList() {
        binding.recView.visibility = View.GONE

        binding.placeholderImage.visibility = View.VISIBLE
        binding.placeholderTextView.visibility = View.VISIBLE
        binding.placeholderTextView2.visibility = View.VISIBLE
    }

    private fun showList() {
        binding.recView.visibility = View.VISIBLE

        binding.placeholderImage.visibility = View.GONE
        binding.placeholderTextView.visibility = View.GONE
        binding.placeholderTextView2.visibility = View.GONE
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

        animation.addAnimatorListener(object : AnimatorListener {
            override fun onAnimationEnd(animation: Animator?) {
                dialog.dismiss()
            }
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

    override fun onClick(taskData: DbTask) {
        navigateToDetail(taskData.id)
    }

    override fun onChecked(taskData: DbTask) {
        viewModel.changeDone(taskData).observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Success -> Timber.i("${taskData.title} done changed")
            }
        }
    }

    private fun lottieDialog(lottieBinding: DialogLoadingBinding): AlertDialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setView(lottieBinding.root)
            .show()
    }
}
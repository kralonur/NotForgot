package com.example.notforgot.ui.task_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notforgot.databinding.FragmentDetailBinding

class TaskDetailFragment : Fragment() {
    private val viewModel by viewModels<TaskDetailViewModel>()
    private lateinit var binding: FragmentDetailBinding
    private val args by navArgs<TaskDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var taskId = args.taskId

        viewModel.getTask(taskId).observe(viewLifecycleOwner) {
            it?.let {
                binding.title.text = it.task.title
                binding.layoutDetail.task = it
                taskId = it.task.id
            }
        }

        binding.materialButton.setOnClickListener {
            navigateToEdit(taskId)
        }

    }

    private fun navigateToEdit(id: Int) {
        findNavController().navigate(
            TaskDetailFragmentDirections.actionTaskDetailFragmentToTaskCreateFragment(id)
        )
    }
}
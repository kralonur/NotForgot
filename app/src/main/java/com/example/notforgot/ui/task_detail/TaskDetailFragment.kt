package com.example.notforgot.ui.task_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notforgot.R
import com.example.notforgot.databinding.LayoutDetailCreateBinding

class TaskDetailFragment : Fragment() {
    private val viewModel by viewModels<TaskDetailViewModel>()
    private lateinit var binding: LayoutDetailCreateBinding
    private val args by navArgs<TaskDetailFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = LayoutDetailCreateBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        setLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var taskId = args.taskId

        binding.materialButton.text = getString(R.string.edit)

        viewModel.getTask(taskId).observe(viewLifecycleOwner) {
            it?.let {
                binding.title.text = it.task.title
                binding.layoutDetail.task = it
                taskId = it.task.id
            }
        }

        binding.materialButton.setOnClickListener {
            viewModel.navigateToEdit(taskId)
        }

        viewModel.navigateEdit.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    TaskDetailFragmentDirections.actionTaskDetailFragmentToTaskCreateFragment(it)
                )
            }
            viewModel.navigateToEditDone()
        }
    }

    private fun setLayout() {
        binding.groupDetail.visibility = View.VISIBLE
    }
}
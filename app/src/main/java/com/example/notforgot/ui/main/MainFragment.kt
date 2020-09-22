package com.example.notforgot.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notforgot.databinding.FragmentMainBinding
import com.example.notforgot.model.items.task.Task
import com.example.notforgot.recview.TaskAdapter
import com.example.notforgot.recview.TaskClickListener

class MainFragment : Fragment(), TaskClickListener {
    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TaskAdapter(this)
        binding.recView.adapter = adapter

        viewModel.taskList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        binding.addButton.setOnClickListener {
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToTaskCreateFragment()
            )
        }
    }

    override fun onClick(task_data: Task) {
        //TODO("Not yet implemented")
    }
}
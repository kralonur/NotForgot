package com.example.notforgot.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.notforgot.databinding.FragmentMainBinding
import com.example.notforgot.model.db.items.DbTask
import com.example.notforgot.recview.TaskAdapter
import com.example.notforgot.recview.TaskClickListener
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

        viewModel.navigateDetail.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToTaskDetailFragment(it.id)
                )
            }
            viewModel.navigateToDetailDone()
        }
    }

    override fun onClick(task_data: DbTask) {
        viewModel.navigateToDetail(task_data)
    }
}
package com.example.notforgot.ui.task_create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notforgot.R
import com.example.notforgot.databinding.FragmentCreateBinding
import com.example.notforgot.model.db.items.DbCategory
import com.example.notforgot.model.db.items.DbPriority
import com.example.notforgot.model.domain.ResultWrapper
import com.example.notforgot.util.*
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber

class TaskCreateFragment : Fragment() {
    private val viewModel by viewModels<TaskCreateViewModel>()
    private lateinit var binding: FragmentCreateBinding
    private val args by navArgs<TaskCreateFragmentArgs>()

    private var taskId: Int = TaskCreateConstants.CREATE_TASK_ID

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCreateBinding.inflate(inflater, container, false)
        binding.layoutCreate.lifecycleOwner = viewLifecycleOwner
        binding.layoutCreate.viewModel = viewModel
        binding.toolbar.setNavigationOnClickListener { tryNavigateUp() }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.taskId.value = args.taskId

        viewModel.taskId.observe(viewLifecycleOwner) {
            taskId = it
        }

        invalidateInput()

        adjustFragment()

        bindCategoryList()

        bindPriorityList()

        binding.layoutCreate.endDate.setOnClickListener { showDatePicker() }

        binding.materialButton.setOnClickListener { trySave() }

        binding.layoutCreate.newCategory.setOnClickListener { showCategoryDialog() }

        postTaskResponse()

        updateTaskResponse()

    }

    private fun adjustFragment() {
        if (viewModel.isNewTask()) adjustFragmentForNewTask()
        else adjustFragmentForUpdateTask()
    }

    private fun adjustFragmentForUpdateTask() {
        binding.title.text = getString(R.string.edit_task)
        binding.materialButton.text = getString(R.string.edit)

        viewModel.getTask().observe(viewLifecycleOwner) {
            it?.let {
                viewModel.updateValuesWithTask(it)
            }
        }
    }

    private fun adjustFragmentForNewTask() {
        binding.title.text = getString(R.string.create_task)
        binding.materialButton.text = getString(R.string.create)
    }

    private fun navigateToDetail(id: Int) {
        findNavController().navigate(
            TaskCreateFragmentDirections.actionTaskCreateFragmentToTaskDetailFragment(id)
        )
    }

    private fun trySave() {
        if (viewModel.validate())
            showSaveDialog()
    }

    private fun updateTaskResponse() {
        viewModel.updateTaskResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> Timber.i("Editing task...")
                is ResultWrapper.Success -> {
                    Timber.i("Task edited with id: $taskId")
                    requireContext().showShortText(getString(R.string.task_edited_successfully))
                    navigateToDetail(taskId)
                }
                else -> requireContext().showShortText(getString(R.string.error_while_editing_task))
            }
        }
    }

    private fun postTaskResponse() {
        viewModel.postTaskResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> Timber.i("Creating task...")
                is ResultWrapper.Success -> {
                    Timber.i("Task created with id: ${it.value}")
                    requireContext().showShortText(getString(R.string.task_created_successfully))
                    navigateToDetail(it.value.toInt())
                }
                else -> requireContext().showShortText(getString(R.string.error_while_creating_task))
            }
        }
    }

    private fun bindPriorityList() {
        val prList = ArrayList<DbPriority>()
        viewModel.getPriorityList().observe(viewLifecycleOwner) {
            it?.let {
                prList.clear()
                prList.addAll(it)
                val items = it.map { item -> item.name }
                val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, items)
                binding.layoutCreate.priority.setAdapter(adapter)
            }
        }

        binding.layoutCreate.priority.setOnItemClickListener { _, _, position, _ ->
            viewModel.priority.postValue(prList[position])
            binding.layoutCreate.textFieldSelectPriority.invalidateError()
        }
    }

    private fun bindCategoryList() {
        val catList = ArrayList<DbCategory>()
        viewModel.getCategoryList().observe(viewLifecycleOwner) {
            it?.let {
                catList.clear()
                catList.addAll(it)
                val items = it.map { item -> item.name }
                val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, items)
                binding.layoutCreate.category.setAdapter(adapter)
            }
        }

        binding.layoutCreate.category.setOnItemClickListener { _, _, position, _ ->
            viewModel.category.postValue(catList[position])
            binding.layoutCreate.textFieldSelectCategory.invalidateError()
        }
    }

    private fun invalidateInput() {
        binding.layoutCreate.let {
            it.title.invalidateError(it.textFieldTitle)
            it.description.invalidateError(it.textFieldDescription)
        }
    }

    private fun showSaveDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.save_q))
            .setNeutralButton(resources.getString(R.string.cancel), null)
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                if (viewModel.isNewTask())
                    viewModel.postTask()
                else
                    viewModel.updateTask()
            }
            .show()
    }

    private fun showDiscardDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.discard_changes_q))
            .setNeutralButton(resources.getString(R.string.cancel), null)
            .setPositiveButton(resources.getString(R.string.discard)) { _, _ ->
                findNavController().navigateUp()
            }
            .show()
    }

    private fun tryNavigateUp() {
        if (viewModel.isChangesMade()) showDiscardDialog()
        else findNavController().navigateUp()
    }

    private fun showDatePicker() {
        val builder: MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()
        val picker: MaterialDatePicker<*> = builder.build()
        picker.show(childFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener { time ->
            (time as Long).let {
                viewModel.deadline.postValue(it.fromMsToEpoch())
                binding.layoutCreate.endDate.setText(it.toDateString())
                binding.layoutCreate.textFieldEndDate.invalidateError()
            }
        }
    }

    private fun showCategoryDialog() {
        findNavController().navigate(
            TaskCreateFragmentDirections.actionTaskCreateFragmentToCategoryDialog()
        )

        getCategoryDialogResult()
    }

    private fun getCategoryDialogResult() {
        getNavigationResult<Long>(findNavController().previousBackStackEntry!!, "category") {
            viewModel.getCategoryById(it.toInt()).observe(viewLifecycleOwner) { category ->
                viewModel.category.postValue(category)
            }
        }
    }

}
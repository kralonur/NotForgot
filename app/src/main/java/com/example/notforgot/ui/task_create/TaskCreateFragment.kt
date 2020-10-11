package com.example.notforgot.ui.task_create

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notforgot.R
import com.example.notforgot.databinding.FragmentCreateBinding
import com.example.notforgot.databinding.LayoutCreateCategoryBinding
import com.example.notforgot.model.db.items.DbCategory
import com.example.notforgot.model.db.items.DbPriority
import com.example.notforgot.model.domain.ResultWrapper
import com.example.notforgot.model.domain.TaskDomain
import com.example.notforgot.util.*
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber

class TaskCreateFragment : Fragment() {
    private val viewModel by viewModels<TaskCreateViewModel>()
    private lateinit var binding: FragmentCreateBinding
    private val args by navArgs<TaskCreateFragmentArgs>()

    private var taskId = TaskCreateConstants.CREATE_TASK_ID
    private var title = ""
    private var description = ""
    private var done = 0
    private var created = 0L
    private var deadline = TaskCreateConstants.EMPTY_DEADLINE
    private var categoryId = TaskCreateConstants.EMPTY_CATEGORY
    private var priorityId = TaskCreateConstants.EMPTY_PRIORITY

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCreateBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        invalidateInput()

        taskId = args.taskId

        if (taskId == TaskCreateConstants.CREATE_TASK_ID) adjustFragmentForNewTask()
        else adjustFragmentForUpdateTask()

        bindCategoryList()

        bindPriorityList()

        binding.layoutCreate.endDate.setOnClickListener { showDatePicker() }

        binding.materialButton.setOnClickListener { trySave() }

        binding.layoutCreate.newCategory.setOnClickListener { showCategoryDialog() }

        postTaskResponse()

        updateTaskResponse()

        inputValidation()

    }

    private fun adjustFragmentForUpdateTask() {
        binding.title.text = getString(R.string.edit_task)
        binding.materialButton.text = getString(R.string.edit)
        viewModel.getTask(taskId).observe(viewLifecycleOwner) {
            it?.let {
                updateLayoutWithTask(it)
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
        val validInput = viewModel.validateInput(
            binding.layoutCreate.title.text.toString(),
            binding.layoutCreate.description.text.toString(),
            deadline,
            categoryId,
            priorityId
        )

        if (validInput)
            showSaveDialog()
    }

    private fun inputValidation() {
        viewModel.inputValidation.observe(viewLifecycleOwner) { list ->
            list.forEach {
                when (it) {
                    TaskCreateValidation.EMPTY_TITLE -> binding.layoutCreate.textFieldTitle.error =
                        getString(R.string.title_cannot_be_empty)
                    TaskCreateValidation.EMPTY_DESCRIPTION -> binding.layoutCreate.textFieldDescription.error =
                        getString(R.string.description_cannot_be_empty)
                    TaskCreateValidation.EMPTY_CATEGORY -> binding.layoutCreate.textFieldSelectCategory.error =
                        getString(R.string.category_cannot_be_empty)
                    TaskCreateValidation.EMPTY_PRIORITY -> binding.layoutCreate.textFieldSelectPriority.error =
                        getString(R.string.priority_cannot_be_empty)
                    TaskCreateValidation.EMPTY_END_DATE -> binding.layoutCreate.textFieldEndDate.error =
                        getString(R.string.end_date_cannot_be_empty)
                }
            }
        }
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

    private fun postCategoryResponse(
        categoryBinding: LayoutCreateCategoryBinding,
        dialog: androidx.appcompat.app.AlertDialog,
    ) {
        viewModel.postCategoryResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> Timber.i("Creating category...")
                is ResultWrapper.Success -> {
                    Timber.i("Category created with id: ${it.value}")
                    requireContext().showShortText(getString(R.string.category_created_successfully))
                    dialog.dismiss()
                }
                else -> categoryBinding.textFieldCategory.error =
                    getString(R.string.error_while_creating_category)
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
            priorityId = prList[position].id
            if (binding.layoutCreate.textFieldSelectPriority.error != null)
                binding.layoutCreate.textFieldSelectPriority.error = null
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
            categoryId = catList[position].id
            if (binding.layoutCreate.textFieldSelectCategory.error != null)
                binding.layoutCreate.textFieldSelectCategory.error = null
        }
    }

    private fun updateLayoutWithTask(task: TaskDomain) {
        binding.layoutCreate.let {
            it.title.setText(task.task.title)
            it.description.setText(task.task.description)
            it.endDate.setText(task.task.deadline.fromEpochToMs()
                .toDateString())
            it.textFieldSelectCategory.hint =
                getString(R.string.current_category, task.category.name)
            it.textFieldSelectPriority.hint =
                getString(R.string.current_priority, task.priority.name)
        }

        title = task.task.title
        description = task.task.description
        done = task.task.done
        created = task.task.created
        deadline = task.task.deadline
        categoryId = task.category.id
        priorityId = task.priority.id
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
                if (taskId == 0)
                    createTask()
                else
                    updateTask()
            }
            .show()
    }

    private fun createTask() {
        viewModel.postTask(binding.layoutCreate.title.text.toString(),
            binding.layoutCreate.description.text.toString(),
            deadline,
            categoryId,
            priorityId)
    }

    private fun updateTask() {
        viewModel.updateTask(taskId,
            binding.layoutCreate.title.text.toString(),
            binding.layoutCreate.description.text.toString(),
            done,
            created,
            deadline,
            categoryId,
            priorityId)
    }

    private fun showDatePicker() {
        val builder: MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()
        val picker: MaterialDatePicker<*> = builder.build()
        picker.show(childFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener { time ->
            (time as Long).let {
                deadline = it.fromMsToEpoch()
                binding.layoutCreate.endDate.setText(it.toDateString())

                if (binding.layoutCreate.textFieldEndDate.error != null)
                    binding.layoutCreate.textFieldEndDate.error = null
            }
        }
    }

    private fun showCategoryDialog() {
        val categoryBinding = LayoutCreateCategoryBinding.inflate(layoutInflater)

        categoryBinding.category.doAfterTextChanged {
            if (categoryBinding.textFieldCategory.error != null)
                categoryBinding.textFieldCategory.error = null
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.create_category))
            .setView(categoryBinding.root)
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .setPositiveButton(resources.getString(R.string.yes), null)
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (categoryBinding.category.text.isNullOrEmpty()) {
                categoryBinding.textFieldCategory.error =
                    getString(R.string.category_name_cannot_be_empty)
            } else {
                createCategory(categoryBinding, dialog)
            }
        }
    }

    private fun createCategory(
        categoryBinding: LayoutCreateCategoryBinding,
        dialog: androidx.appcompat.app.AlertDialog,
    ) {
        viewModel.postCategory(categoryBinding.category.text.toString())

        postCategoryResponse(categoryBinding, dialog)
    }
}
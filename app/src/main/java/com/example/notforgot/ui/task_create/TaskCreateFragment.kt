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
import com.example.notforgot.model.ResultWrapper
import com.example.notforgot.model.TaskDomain
import com.example.notforgot.model.db.items.DbCategory
import com.example.notforgot.model.db.items.DbPriority
import com.example.notforgot.model.db.items.DbTask
import com.example.notforgot.util.fromEpochToMs
import com.example.notforgot.util.fromMsToEpoch
import com.example.notforgot.util.showShortText
import com.example.notforgot.util.toDateString
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber

class TaskCreateFragment : Fragment() {
    private val viewModel by viewModels<TaskCreateViewModel>()
    private lateinit var binding: FragmentCreateBinding
    private val args by navArgs<TaskCreateFragmentArgs>()

    private lateinit var taskToUpdate: TaskDomain
    private var taskId: Int = 0

    private var selectedCategory: DbCategory? = null
    private var selectedPriority: DbPriority? = null
    private var deadline: Long? = null

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

        if (taskId == 0) {
            binding.title.text = getString(R.string.create_task)
            binding.materialButton.text = getString(R.string.create)
        } else {
            binding.title.text = getString(R.string.edit_task)
            binding.materialButton.text = getString(R.string.edit)
            viewModel.getTask(taskId).observe(viewLifecycleOwner) {
                it?.let {
                    updateLayoutWithTask(it)
                    taskToUpdate = it
                    selectedCategory = it.category
                    selectedPriority = it.priority
                    deadline = it.task.deadline
                }
            }
        }

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
            selectedCategory = catList[position]
            if (binding.layoutCreate.textFieldSelectCategory.error != null)
                binding.layoutCreate.textFieldSelectCategory.error = null
        }

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
            selectedPriority = prList[position]
            if (binding.layoutCreate.textFieldSelectPriority.error != null)
                binding.layoutCreate.textFieldSelectPriority.error = null
        }


        binding.layoutCreate.endDate.setOnClickListener { showDatePicker() }

        binding.materialButton.setOnClickListener {
            if (checkInput())
                showSaveDialog()
        }

        binding.layoutCreate.newCategory.setOnClickListener {
            showCategoryDialog()
        }

        viewModel.navigateDetail.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(
                    TaskCreateFragmentDirections.actionTaskCreateFragmentToTaskDetailFragment(it)
                )
            }
            viewModel.navigateToDetailDone()
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
    }

    private fun checkInput(): Boolean {
        var result = true

        if (binding.layoutCreate.title.text.isNullOrEmpty()) {
            binding.layoutCreate.textFieldTitle.error = getString(R.string.title_cannot_be_empty)
            result = false
        }

        if (binding.layoutCreate.description.text.isNullOrEmpty()) {
            binding.layoutCreate.textFieldDescription.error =
                getString(R.string.description_cannot_be_empty)
            result = false
        }

        if (selectedCategory == null) {
            binding.layoutCreate.textFieldSelectCategory.error =
                getString(R.string.category_cannot_be_empty)
            result = false
        }

        if (selectedPriority == null) {
            binding.layoutCreate.textFieldSelectPriority.error =
                getString(R.string.priority_cannot_be_empty)
            result = false
        }

        if (deadline == null) {
            binding.layoutCreate.textFieldEndDate.error =
                getString(R.string.end_date_cannot_be_empty)
            result = false
        }

        return result
    }

    private fun invalidateInput() {
        binding.layoutCreate.let {
            it.title.doAfterTextChanged { _ ->
                if (it.textFieldTitle.error != null)
                    it.textFieldTitle.error = null
            }

            it.description.doAfterTextChanged { _ ->
                if (it.textFieldDescription.error != null)
                    it.textFieldDescription.error = null
            }
        }

    }

    private fun showSaveDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.save_q))
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
            requireNotNull(deadline),
            requireNotNull(selectedCategory).id,
            requireNotNull(selectedPriority).id).observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> Timber.i("Creating task...")
                is ResultWrapper.Error -> requireContext().showShortText(getString(R.string.error_while_creating_task))
                is ResultWrapper.Success -> {
                    Timber.i("Task created with id: ${it.value}")
                    requireContext().showShortText(getString(R.string.task_created_successfully))
                    viewModel.navigateToDetail(it.value.toInt())
                }
            }
        }
    }

    private fun updateTask() {
        val task = DbTask(
            taskToUpdate.task.id,
            binding.layoutCreate.title.text.toString(),
            binding.layoutCreate.description.text.toString(),
            taskToUpdate.task.done,
            taskToUpdate.task.created,
            requireNotNull(this@TaskCreateFragment.deadline),
            requireNotNull(selectedCategory).id,
            requireNotNull(selectedPriority).id
        )

        viewModel.updateTask(task).observe(viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> Timber.i("Editing task...")
                is ResultWrapper.Error -> requireContext().showShortText(getString(R.string.error_while_editing_task))
                is ResultWrapper.Success -> {
                    Timber.i("Task edited with id: ${task.id}")
                    requireContext().showShortText(getString(R.string.task_edited_successfully))
                    viewModel.navigateToDetail(taskToUpdate.task.id)
                }
            }
        }

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
            .observe(viewLifecycleOwner) {
                when (it) {
                    is ResultWrapper.Loading -> Timber.i("Creating category...")
                    is ResultWrapper.Error -> categoryBinding.textFieldCategory.error =
                        getString(R.string.error_while_creating_category)
                    is ResultWrapper.Success -> {
                        Timber.i("Category created with id: ${it.value}")
                        requireContext().showShortText(getString(R.string.category_created_successfully))
                        dialog.dismiss()
                    }
                }
            }
    }
}
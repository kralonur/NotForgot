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
import com.example.notforgot.R
import com.example.notforgot.databinding.LayoutDetailCreateBinding
import com.example.notforgot.model.items.category.Category
import com.example.notforgot.model.items.category.CategoryPost
import com.example.notforgot.model.items.priority.Priority
import com.example.notforgot.model.items.task.TaskPost
import com.example.notforgot.util.fromMsToEpoch
import com.example.notforgot.util.showShortText
import com.example.notforgot.util.toDateString
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class TaskCreateFragment : Fragment() {
    private val viewModel by viewModels<TaskCreateViewModel>()
    private lateinit var binding: LayoutDetailCreateBinding

    private var selectedCategory: Category? = null
    private var selectedPriority: Priority? = null
    private var deadline: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = LayoutDetailCreateBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        setLayout()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.title.text = getString(R.string.create_task)
        binding.materialButton.text = getString(R.string.create)

        binding.layoutCreate.description.doAfterTextChanged {
            if (binding.layoutCreate.filledTextField.error != null)
                binding.layoutCreate.filledTextField.error = null
        }

        viewModel.categoryList.observe(viewLifecycleOwner) {
            it?.let {
                val items = it.map { item -> item.name }
                val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, items)
                binding.layoutCreate.category.setAdapter(adapter)
            }
        }

        binding.layoutCreate.category.setOnItemClickListener { _, _, position, _ ->
            selectedCategory = viewModel.categoryList.value?.get(position)
            if (binding.layoutCreate.textInputLayout.error != null)
                binding.layoutCreate.textInputLayout.error = null
        }

        viewModel.priorityList.observe(viewLifecycleOwner) {
            it?.let {
                val items = it.map { item -> item.name }
                val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, items)
                binding.layoutCreate.priority.setAdapter(adapter)
            }
        }

        binding.layoutCreate.priority.setOnItemClickListener { _, _, position, _ ->
            selectedPriority = viewModel.priorityList.value?.get(position)
            if (binding.layoutCreate.textInputLayout2.error != null)
                binding.layoutCreate.textInputLayout2.error = null
        }


        binding.layoutCreate.endDate.setOnClickListener { showDatePicker() }

        binding.materialButton.setOnClickListener {
            if (checkInput())
                showSaveDialog()
        }

        binding.layoutCreate.newCategory.setOnClickListener {
            showCategoryDialog()
        }

    }

    private fun setLayout() {
        binding.groupCreate.visibility = View.VISIBLE
    }

    private fun checkInput(): Boolean {
        var result = true

        if (binding.layoutCreate.title.text.isNullOrEmpty()) {
            requireContext().showShortText("Title cannot be empty!")
            result = false
        }

        if (binding.layoutCreate.description.text.isNullOrEmpty()) {
            binding.layoutCreate.filledTextField.error = "Description cannot be empty!"
            result = false
        }

        if (selectedCategory == null) {
            binding.layoutCreate.textInputLayout.error = "Category cannot be empty!"
            result = false
        }

        if (selectedPriority == null) {
            binding.layoutCreate.textInputLayout2.error = "Priority cannot be empty!"
            result = false
        }

        if (deadline == null) {
            requireContext().showShortText("End date cannot be empty!")
            result = false
        }

        return result
    }

    private fun showSaveDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(resources.getString(R.string.save_q))
            .setNeutralButton(resources.getString(R.string.cancel)) { _, _ ->
                // Respond to neutral button press
            }
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                val taskNew = TaskPost(
                    binding.layoutCreate.title.text.toString(),
                    binding.layoutCreate.description.text.toString(),
                    0,
                    requireNotNull(deadline),
                    requireNotNull(selectedCategory).id,
                    requireNotNull(selectedPriority).id
                )
                viewModel.postTask(taskNew)
            }
            .show()
    }

    private fun showDatePicker() {
        val builder: MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker() // 1
        val picker: MaterialDatePicker<*> = builder.build()
        picker.show(childFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener { time ->
            (time as Long).let {
                deadline = it.fromMsToEpoch()
                binding.layoutCreate.endDate.setText(it.toDateString())
            }
        }
    }

    private fun showCategoryDialog() {
        val customView =
            parentFragment?.layoutInflater?.inflate(R.layout.layout_create_category, null)
        val filledTextView = customView?.findViewById<TextInputLayout>(R.id.filledTextField)
        val category = customView?.findViewById<TextInputEditText>(R.id.category)

        category?.doAfterTextChanged {
            if (filledTextView?.error != null)
                filledTextView.error = null
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.create_category))
            .setView(customView)
            .setNegativeButton(resources.getString(R.string.cancel), null)
            .setPositiveButton(resources.getString(R.string.yes), null)
            .show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if (category?.text.isNullOrEmpty()) {
                filledTextView?.error = "Category name cannot be empty!"
            } else {
                val categoryNew = CategoryPost(category?.text.toString())
                viewModel.postCategory(categoryNew)
                dialog.dismiss()
            }
        }
    }
}
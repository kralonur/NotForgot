package com.example.notforgot.ui.task_create

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.notforgot.R
import com.example.notforgot.databinding.LayoutCreateCategoryBinding
import com.example.notforgot.model.domain.ResultWrapper
import com.example.notforgot.util.invalidateError
import com.example.notforgot.util.setNavigationResult
import com.example.notforgot.util.showShortText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import timber.log.Timber

class CategoryDialog : DialogFragment() {
    private val viewModel by viewModels<CategoryViewModel>()
    private lateinit var binding: LayoutCreateCategoryBinding
    private lateinit var dialog: AlertDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = LayoutCreateCategoryBinding.inflate(layoutInflater)

        val builder = MaterialAlertDialogBuilder(requireContext())

        builder.apply {
            setTitle(resources.getString(R.string.create_category))
            setView(binding.root)
            setNegativeButton(resources.getString(R.string.cancel), null)
            setPositiveButton(resources.getString(R.string.yes), null)
        }

        dialog = builder.create()

        dialog.setOnShowListener {
            binding.category.invalidateError(binding.textFieldCategory)

            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                if (binding.category.text.isNullOrEmpty()) {
                    binding.textFieldCategory.error =
                        getString(R.string.category_name_cannot_be_empty)
                } else {
                    createCategory()
                }
            }

        }

        return dialog
    }

    private fun createCategory() {
        viewModel.postCategory(binding.category.text.toString())

        postCategoryResponse()
    }

    private fun postCategoryResponse() {
        viewModel.postCategoryResponse.observe(requireParentFragment().viewLifecycleOwner) {
            when (it) {
                is ResultWrapper.Loading -> Timber.i("Creating category...")
                is ResultWrapper.Success -> {
                    Timber.i("Category created with id: ${it.value}")
                    requireContext().showShortText(getString(R.string.category_created_successfully))
                    setNavigationResult("category", it.value)
                    dismiss()
                }
                else -> binding.textFieldCategory.error =
                    getString(R.string.error_while_creating_category)
            }
        }

    }
}
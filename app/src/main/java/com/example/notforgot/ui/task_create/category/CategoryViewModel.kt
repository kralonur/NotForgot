package com.example.notforgot.ui.task_create.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.notforgot.R
import com.example.notforgot.model.db.items.DbCategory
import com.example.notforgot.model.domain.ResultWrapper
import com.example.notforgot.repository.ItemsRepository
import com.example.notforgot.util.Constants
import com.example.notforgot.util.SharedPref
import com.example.notforgot.util.getNonNullValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ItemsRepository(getApplication<Application>().applicationContext)

    private val _postCategoryResponse = MutableLiveData<ResultWrapper<Long>>()
    val postCategoryResponse: LiveData<ResultWrapper<Long>>
        get() = _postCategoryResponse

    val categoryName = MutableLiveData("")

    val categoryError = MutableLiveData("")

    private fun postCategory() {
        val category = createDbCategory()
        viewModelScope.launch {
            withContext(Dispatchers.IO + viewModelScope.coroutineContext) {
                repo.addCategory(category).collect { _postCategoryResponse.postValue(it) }
            }
        }
    }

    fun tryPostCategory() {
        if (validate()) postCategory()
    }

    private fun validate(): Boolean {
        val categoryValidation = validateCategoryName(categoryName.getNonNullValue())

        categoryError.postValue(categoryValidation)

        return categoryValidation.isEmpty()
    }

    private fun validateCategoryName(name: String): String {
        return if (name.isEmpty() || name.isBlank())
            getApplication<Application>().applicationContext.getString(R.string.category_name_cannot_be_empty)
        else if (name.length > Constants.MAX_CATEGORY_NAME_LENGTH)
            getApplication<Application>().applicationContext.getString(R.string.cannot_exceed_maximum_character_limit)
        else ""
    }

    private fun createDbCategory() =
        DbCategory(
            SharedPref.getCategoryId(getApplication<Application>().applicationContext),
            categoryName.getNonNullValue()
        )
}
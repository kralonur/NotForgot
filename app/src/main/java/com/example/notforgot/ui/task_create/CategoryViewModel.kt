package com.example.notforgot.ui.task_create

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.notforgot.model.db.items.DbCategory
import com.example.notforgot.model.domain.ResultWrapper
import com.example.notforgot.repository.ItemsRepository
import com.example.notforgot.util.SharedPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ItemsRepository(getApplication<Application>().applicationContext)

    private val _postCategoryResponse = MutableLiveData<ResultWrapper<Long>>()
    val postCategoryResponse: LiveData<ResultWrapper<Long>>
        get() = _postCategoryResponse

    fun postCategory(name: String) {
        val category = createDbCategory(name)
        viewModelScope.launch {
            withContext(Dispatchers.IO + viewModelScope.coroutineContext) {
                repo.addCategory(category).collect { _postCategoryResponse.postValue(it) }
            }
        }
    }

    private fun createDbCategory(name: String) =
        DbCategory(SharedPref.getCategoryId(getApplication<Application>().applicationContext), name)
}
package com.example.notforgot.ui.task_create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notforgot.api.NetworkService
import com.example.notforgot.model.items.category.Category
import com.example.notforgot.model.items.category.CategoryPost
import com.example.notforgot.model.items.priority.Priority
import com.example.notforgot.model.items.task.TaskPost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class TaskCreateViewModel : ViewModel() {
    private val api = NetworkService.itemsService

    private val _categoryList = MutableLiveData<List<Category>>()
    val categoryList: LiveData<List<Category>>
        get() = _categoryList

    private val _priorityList = MutableLiveData<List<Priority>>()
    val priorityList: LiveData<List<Priority>>
        get() = _priorityList

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api.getCategories()
                _categoryList.postValue(response)
                Timber.i(response.toString())
            } catch (e: Throwable) {
                when (e) {
                    is HttpException -> Timber.e(e.code().toString())
                    else -> Timber.e(e)
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api.getPriorities()
                _priorityList.postValue(response)
                Timber.i(response.toString())
            } catch (e: Throwable) {
                when (e) {
                    is HttpException -> Timber.e(e.code().toString())
                    else -> Timber.e(e)
                }
            }
        }
    }

    fun postTask(task: TaskPost) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api.postTask(task)
                Timber.i(response.toString())
            } catch (e: Throwable) {
                Timber.e(e)
                when (e) {
                    is HttpException -> Timber.e(e.code().toString())
                    else -> Timber.e(e)
                }
            }
        }
    }

    fun postCategory(category: CategoryPost) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api.postCategory(category)
                Timber.i(response.toString())
            } catch (e: Throwable) {
                Timber.e(e)
                when (e) {
                    is HttpException -> Timber.e(e.code().toString())
                    else -> Timber.e(e)
                }
            }
        }
    }
}
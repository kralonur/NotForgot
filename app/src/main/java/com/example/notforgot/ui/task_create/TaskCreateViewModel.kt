package com.example.notforgot.ui.task_create

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.notforgot.model.ResultWrapper
import com.example.notforgot.model.db.items.DbCategory
import com.example.notforgot.model.db.items.DbTask
import com.example.notforgot.repository.ItemsRepository
import com.example.notforgot.util.SharedPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import timber.log.Timber

class TaskCreateViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val repo = ItemsRepository(context)

    fun getCategoryList() = repo.getCategoryList().catch { Timber.e(it) }
        .asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)

    fun getPriorityList() = repo.getPriorityList().catch { Timber.e(it) }
        .asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)

    fun postTask(
        title: String,
        description: String,
        deadline: Long,
        categoryId: Int,
        priorityId: Int,
    ): LiveData<ResultWrapper<Long>> {
        val task = DbTask(SharedPref.getTaskId(context),
            title,
            description,
            0,
            0,
            deadline,
            categoryId,
            priorityId)
        return repo.addTask(task).asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)
    }

    fun postCategory(name: String): LiveData<ResultWrapper<Long>> {
        val category = DbCategory(SharedPref.getCategoryId(context), name)
        return repo.addCategory(category)
            .asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)
    }

}
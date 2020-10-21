package com.example.notforgot.ui.task_detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.notforgot.repository.ItemsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import timber.log.Timber

class TaskDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ItemsRepository(getApplication<Application>().applicationContext)

    fun getTask(taskId: Int) = repo.getTaskDomain(taskId).catch { Timber.e(it) }.asLiveData(
        Dispatchers.IO + viewModelScope.coroutineContext
    )

}
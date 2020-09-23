package com.example.notforgot.ui.task_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.notforgot.api.NetworkService
import com.example.notforgot.model.items.task.Task
import retrofit2.HttpException
import timber.log.Timber

class TaskDetailViewModel : ViewModel() {
    private val api = NetworkService.itemsService

    fun getTask(task_id: Int): LiveData<Task> {
        return liveData {
            try {
                emit(api.getTasks().first { it.id == task_id })
            } catch (e: Throwable) {
                when (e) {
                    is HttpException -> Timber.e(e.code().toString())
                    else -> Timber.e(e)
                }
            }
        }
    }
}
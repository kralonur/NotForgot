package com.example.notforgot.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notforgot.api.NetworkService
import com.example.notforgot.model.items.task.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber

class MainViewModel : ViewModel() {
    private val api = NetworkService.itemsService

    private val _taskList = MutableLiveData<List<Task>>()
    val taskList: LiveData<List<Task>>
        get() = _taskList

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = api.getTasks()
                _taskList.postValue(response)
                Timber.i(response.toString())
            } catch (e: Throwable) {
                when (e) {
                    is HttpException -> Timber.e(e.code().toString())
                    else -> Timber.e(e)
                }
            }
        }
    }
}
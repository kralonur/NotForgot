package com.example.notforgot.ui.task_detail

import android.app.Application
import androidx.lifecycle.*
import com.example.notforgot.repository.ItemsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import timber.log.Timber

class TaskDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val repo = ItemsRepository(context)

    private val _navigateEdit = MutableLiveData<Int?>()
    val navigateEdit: LiveData<Int?>
        get() = _navigateEdit

    fun getTask(task_id: Int) = repo.getTaskDomain(task_id).catch { Timber.e(it) }.asLiveData(
        Dispatchers.IO + viewModelScope.coroutineContext)

    fun navigateToEdit(id: Int) {
        _navigateEdit.postValue(id)
    }

    fun navigateToEditDone() {
        _navigateEdit.postValue(null)
    }
}
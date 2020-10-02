package com.example.notforgot.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.example.notforgot.model.db.items.DbTask
import com.example.notforgot.model.domain.ResultWrapper
import com.example.notforgot.repository.ItemsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import timber.log.Timber

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _navigateDetail = MutableLiveData<DbTask?>()
    val navigateDetail: LiveData<DbTask?>
        get() = _navigateDetail

    private val context = getApplication<Application>().applicationContext
    private val repo = ItemsRepository(context)

    fun getRecviewItemList() =
        repo.getRecviewItemList().catch { Timber.e(it) }
            .asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)

    fun uploadToCloud() =
        repo.uploadToCloud().asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)

    fun deleteTask(task: DbTask) = repo.deleteTask(task).asLiveData()

    fun changeDone(task: DbTask): LiveData<ResultWrapper<Unit>> {
        val changedDone = if (task.done == 0) 1 else 0

        task.done = changedDone

        return repo.updateTask(task).asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)
    }

    fun navigateToDetail(task: DbTask) {
        _navigateDetail.postValue(task)
    }

    fun navigateToDetailDone() {
        _navigateDetail.postValue(null)
    }
}
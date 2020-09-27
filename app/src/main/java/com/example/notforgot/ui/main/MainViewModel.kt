package com.example.notforgot.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.example.notforgot.model.db.items.DbTask
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

    fun getTaskList() =
        repo.getTaskDomainList().catch { Timber.e(it) }
            .asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)

    fun navigateToDetail(task: DbTask) {
        _navigateDetail.postValue(task)
    }

    fun navigateToDetailDone() {
        _navigateDetail.postValue(null)
    }
}
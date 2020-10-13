package com.example.notforgot.ui.task_create

import android.app.Application
import androidx.lifecycle.*
import com.example.notforgot.model.db.items.DbTask
import com.example.notforgot.model.domain.ResultWrapper
import com.example.notforgot.repository.ItemsRepository
import com.example.notforgot.util.SharedPref
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class TaskCreateViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ItemsRepository(getApplication<Application>().applicationContext)

    private val _inputValidation = MutableLiveData<List<TaskCreateValidation>>()
    val inputValidation: LiveData<List<TaskCreateValidation>>
        get() = _inputValidation

    private val _postTaskResponse = MutableLiveData<ResultWrapper<Long>>()
    val postTaskResponse: LiveData<ResultWrapper<Long>>
        get() = _postTaskResponse

    private val _updateTaskResponse = MutableLiveData<ResultWrapper<Unit>>()
    val updateTaskResponse: LiveData<ResultWrapper<Unit>>
        get() = _updateTaskResponse

    fun getCategoryList() = repo.getCategoryList().catch { Timber.e(it) }
        .asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)

    fun getPriorityList() = repo.getPriorityList().catch { Timber.e(it) }
        .asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)

    fun getTask(id: Int) =
        repo.getTaskDomain(id).asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)

    fun validateInput(
        title: String,
        description: String,
        deadline: Long,
        categoryId: Int,
        priorityId: Int,
    ): Boolean {
        val validationList = ArrayList<TaskCreateValidation>()

        if (title.isEmpty() || title.isBlank()) {
            validationList.add(TaskCreateValidation.EMPTY_TITLE)
        }

        if (description.isEmpty() || description.isBlank()) {
            validationList.add(TaskCreateValidation.EMPTY_DESCRIPTION)
        }

        if (deadline == TaskCreateConstants.EMPTY_DEADLINE) {
            validationList.add(TaskCreateValidation.EMPTY_END_DATE)
        }

        if (categoryId == TaskCreateConstants.EMPTY_CATEGORY) {
            validationList.add(TaskCreateValidation.EMPTY_CATEGORY)
        }

        if (priorityId == TaskCreateConstants.EMPTY_PRIORITY) {
            validationList.add(TaskCreateValidation.EMPTY_PRIORITY)
        }

        _inputValidation.postValue(validationList)
        return validationList.isEmpty()
    }

    fun isChangesMade(
        title: String,
        description: String,
        done: Int,
        created: Long,
        deadline: Long,
        categoryId: Int,
        priorityId: Int,
    ): Boolean {
        if (title != "") return true
        if (description != "") return true
        if (done != 0) return true
        if (created != 0L) return true
        if (deadline != TaskCreateConstants.EMPTY_DEADLINE) return true
        if (categoryId != TaskCreateConstants.EMPTY_CATEGORY) return true
        if (priorityId != TaskCreateConstants.EMPTY_PRIORITY) return true

        return false
    }

    fun postTask(
        title: String,
        description: String,
        deadline: Long,
        categoryId: Int,
        priorityId: Int,
    ) {
        val task = createDbTask(
            title = title,
            description = description,
            deadline = deadline,
            categoryId = categoryId,
            priorityId = priorityId
        )

        viewModelScope.launch {
            withContext(Dispatchers.IO + viewModelScope.coroutineContext) {
                repo.addTask(task).collect { _postTaskResponse.postValue(it) }
            }
        }
    }

    private fun createDbTask(
        taskId: Int = SharedPref.getTaskId(getApplication<Application>().applicationContext),
        title: String,
        description: String,
        done: Int = 0,
        created: Long = 0,
        deadline: Long,
        categoryId: Int,
        priorityId: Int,
    ) = DbTask(
        taskId,
        title,
        description,
        done,
        created,
        deadline,
        categoryId,
        priorityId
    )

    fun updateTask(
        taskId: Int,
        title: String,
        description: String,
        done: Int,
        created: Long,
        deadline: Long,
        categoryId: Int,
        priorityId: Int,
    ) {
        val task = createDbTask(
            taskId,
            title,
            description,
            done,
            created,
            deadline,
            categoryId,
            priorityId
        )

        viewModelScope.launch {
            withContext(Dispatchers.IO + viewModelScope.coroutineContext) {
                repo.updateTask(task).collect { _updateTaskResponse.postValue(it) }
            }
        }
    }

    fun getCategoryById(categoryId: Int) = repo.getFlowCategoryById(categoryId)
        .asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)


}
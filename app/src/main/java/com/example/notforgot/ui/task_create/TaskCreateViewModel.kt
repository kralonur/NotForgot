package com.example.notforgot.ui.task_create

import android.app.Application
import androidx.lifecycle.*
import com.example.notforgot.R
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
        validation: TaskCreateValidation
    ): Boolean {
        var valid = true

        if (title.isEmpty() || title.isBlank()) {
            valid = false
            validation.validateTitle(getApplication<Application>().applicationContext.getString(R.string.title_cannot_be_empty))
        }

        if (description.isEmpty() || description.isBlank()) {
            valid = false
            validation.validateDescription(
                getApplication<Application>().applicationContext.getString(
                    R.string.description_cannot_be_empty
                )
            )
        }

        if (deadline == TaskCreateConstants.EMPTY_DEADLINE) {
            valid = false
            validation.validateEndDate(getApplication<Application>().applicationContext.getString(R.string.end_date_cannot_be_empty))
        }

        if (categoryId == TaskCreateConstants.EMPTY_CATEGORY) {
            valid = false
            validation.validateCategory(getApplication<Application>().applicationContext.getString(R.string.category_cannot_be_empty))
        }

        if (priorityId == TaskCreateConstants.EMPTY_PRIORITY) {
            valid = false
            validation.validatePriority(getApplication<Application>().applicationContext.getString(R.string.priority_cannot_be_empty))
        }

        return valid
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
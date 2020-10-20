package com.example.notforgot.ui.task_create

import android.app.Application
import androidx.lifecycle.*
import com.example.notforgot.R
import com.example.notforgot.model.db.items.DbCategory
import com.example.notforgot.model.db.items.DbPriority
import com.example.notforgot.model.db.items.DbTask
import com.example.notforgot.model.domain.ResultWrapper
import com.example.notforgot.model.domain.TaskDomain
import com.example.notforgot.repository.ItemsRepository
import com.example.notforgot.util.*
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

    val taskId = MutableLiveData(TaskCreateConstants.CREATE_TASK_ID)
    val title = MutableLiveData("")
    val description = MutableLiveData("")
    val done = MutableLiveData(0)
    val created = MutableLiveData(0L)
    val deadline = MutableLiveData(TaskCreateConstants.EMPTY_DEADLINE)
    val deadlineText = MediatorLiveData<String>().apply {
        addSource(deadline) {
            value = it.fromEpochToMs().toDateString()
        }
    }
    val category = MutableLiveData(TaskCreateConstants.EMPTY_CATEGORY)
    val priority = MutableLiveData(TaskCreateConstants.EMPTY_PRIORITY)

    val titleError = MutableLiveData("")
    val descriptionError = MutableLiveData("")
    val endDateError = MutableLiveData("")
    val categoryError = MutableLiveData("")
    val priorityError = MutableLiveData("")

    fun getCategoryList() = repo.getCategoryList().catch { Timber.e(it) }
        .asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)

    fun getPriorityList() = repo.getPriorityList().catch { Timber.e(it) }
        .asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)

    fun getTask() =
        repo.getTaskDomain(taskId.getNonNullValue())
            .asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)

    fun updateValuesWithTask(task: TaskDomain) {
        title.postValue(task.task.title)
        description.postValue(task.task.description)
        done.postValue(task.task.done)
        created.postValue(task.task.created)
        deadline.postValue(task.task.deadline)
        category.postValue(task.category)
        priority.postValue(task.priority)
    }

    fun validate(): Boolean {
        val titleValidation = validateTitle(title.getNonNullValue())
        val descriptionValidation = validateDescription(description.getNonNullValue())
        val deadlineValidation = validateDeadline(deadline.getNonNullValue())
        val categoryValidation = validateCategory(category.getNonNullValue())
        val priorityValidation = validatePriority(priority.getNonNullValue())

        titleError.postValue(titleValidation)
        descriptionError.postValue(descriptionValidation)
        endDateError.postValue(deadlineValidation)
        categoryError.postValue(categoryValidation)
        priorityError.postValue(priorityValidation)

        return titleValidation.isEmpty() && descriptionValidation.isEmpty()
                && deadlineValidation.isEmpty() && categoryValidation.isEmpty()
                && priorityValidation.isEmpty()
    }

    private fun validateTitle(title: String): String {
        return if (title.isEmpty() || title.isBlank())
            getApplication<Application>().applicationContext.getString(R.string.title_cannot_be_empty)
        else ""
    }

    private fun validateDescription(description: String): String {
        return if (description.isEmpty() || description.isBlank())
            getApplication<Application>().applicationContext.getString(R.string.description_cannot_be_empty)
        else if (description.length > Constants.MAX_DESCRIPTION_LENGTH)
            getApplication<Application>().applicationContext.getString(R.string.cannot_exceed_maximum_character_limit)
        else ""
    }

    private fun validateDeadline(deadline: Long): String {
        return if (deadline == TaskCreateConstants.EMPTY_DEADLINE)
            getApplication<Application>().applicationContext.getString(R.string.end_date_cannot_be_empty)
        else ""
    }

    private fun validateCategory(category: DbCategory): String {
        return if (category == TaskCreateConstants.EMPTY_CATEGORY)
            getApplication<Application>().applicationContext.getString(R.string.category_cannot_be_empty)
        else ""
    }

    private fun validatePriority(priority: DbPriority): String {
        return if (priority == TaskCreateConstants.EMPTY_PRIORITY)
            getApplication<Application>().applicationContext.getString(R.string.priority_cannot_be_empty)
        else ""
    }

    fun isChangesMade(): Boolean {
        if (title.getNonNullValue() != "") return true
        if (description.getNonNullValue() != "") return true
        if (done.getNonNullValue() != 0) return true
        if (created.getNonNullValue() != 0L) return true
        if (created.getNonNullValue() != TaskCreateConstants.EMPTY_DEADLINE) return true
        if (category.getNonNullValue() != TaskCreateConstants.EMPTY_CATEGORY) return true
        if (priority.getNonNullValue() != TaskCreateConstants.EMPTY_PRIORITY) return true

        return false
    }

    fun postTask() {
        val task = createDbTask()

        viewModelScope.launch {
            withContext(Dispatchers.IO + viewModelScope.coroutineContext) {
                repo.addTask(task).collect { _postTaskResponse.postValue(it) }
            }
        }
    }

    private fun createDbTask() = DbTask(
        if (isNewTask()) getNewIdForTask() else taskId.getNonNullValue(),
        title.getNonNullValue(),
        description.getNonNullValue(),
        if (isNewTask()) 0 else done.getNonNullValue(),
        if (isNewTask()) 0 else created.getNonNullValue(),
        deadline.getNonNullValue(),
        category.getNonNullValue().id,
        priority.getNonNullValue().id
    )

    private fun getNewIdForTask() =
        SharedPref.getTaskId(getApplication<Application>().applicationContext)

    fun isNewTask() = taskId.getNonNullValue() == TaskCreateConstants.CREATE_TASK_ID

    fun updateTask() {
        val task = createDbTask()

        viewModelScope.launch {
            withContext(Dispatchers.IO + viewModelScope.coroutineContext) {
                repo.updateTask(task).collect { _updateTaskResponse.postValue(it) }
            }
        }
    }

    fun getCategoryById(categoryId: Int) = repo.getFlowCategoryById(categoryId)
        .asLiveData(Dispatchers.IO + viewModelScope.coroutineContext)

}
package com.example.notforgot.repository

import android.content.Context
import com.example.notforgot.api.NetworkService
import com.example.notforgot.database.AppDatabase
import com.example.notforgot.model.db.DbLog
import com.example.notforgot.model.db.LogModel
import com.example.notforgot.model.db.LogType
import com.example.notforgot.model.db.items.DbCategory
import com.example.notforgot.model.db.items.DbTask
import com.example.notforgot.model.domain.RecviewItem
import com.example.notforgot.model.mapper.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class ItemsRepository(context: Context) : BaseRepository() {
    private val api = NetworkService.getItemsService(context)
    private val db = AppDatabase.getInstance(context)

    fun fetchFromCloud() = flowCall {
        val categories = api.getCategories().map { CategoryRemoteToDbMapper.map(it) }
        val priorities = api.getPriorities().map { PriorityRemoteToDbMapper.map(it) }
        val tasks = api.getTasks().map { TaskRemoteToDbMapper.map(it) }

        db.categoryDao().insertAll(categories)
        db.priorityDao().insertAll(priorities)
        db.taskDao().insertAll(tasks)
    }

    fun uploadToCloud() = flowCall {
        uploadCategories()
        uploadTasks()
    }

    fun addCategory(category: DbCategory) = flowCall {
        val returnVal = db.categoryDao().insertCategory(category)
        Timber.i(returnVal.toString())
        logAddCategory(returnVal.toInt())

        returnVal
    }

    fun addTask(task: DbTask) = flowCall {
        val returnVal = db.taskDao().insertTask(task)
        Timber.i(returnVal.toString())
        logAddTask(returnVal.toInt())

        returnVal
    }

    fun updateTask(task: DbTask) = flowCall {
        db.taskDao().update(task)
        logUpdateTask(task.id)
    }

    fun deleteTask(task: DbTask) = flowCall {
        db.taskDao().delete(task)
        logDeleteTask(task.id)
    }

    fun getTaskDomain(id: Int) = db.taskDao().getTaskDomainById(id)

    fun getRecviewItemList(): Flow<List<RecviewItem>> =
        db.taskDao().getAllDomain().map { TaskDomainListToRecviewItemListMapper.map(it) }

    fun getCategoryList() = db.categoryDao().getAll()

    fun getPriorityList() = db.priorityDao().getAll()

//CLOUD

    private suspend fun uploadCategories() {
        db.logDao().getAllByModel(LogModel.CATEGORY).forEach {
            val category = db.categoryDao().getCategoryById(it.model_id)
            if (it.logType == LogType.INSERT) cloudPostCategory(category)
            db.logDao().delete(it)
        }
    }

    private suspend fun uploadTasks() {
        db.logDao().getAllByModel(LogModel.TASK).forEach {
            val task = db.taskDao().getTaskById(it.model_id)
            when (it.logType) {
                LogType.INSERT -> cloudPostTask(task)
                LogType.UPDATE -> cloudPatchTask(task)
                LogType.DELETE -> cloudDeleteTask(it.model_id)
            }
            db.logDao().delete(it)
        }
    }

    private suspend fun cloudPostCategory(dbCategory: DbCategory) {
        val category = api.postCategory(CategoryDbToRemotePostMapper.map(dbCategory))
        val newCategoryId = db.categoryDao().insertCategory(CategoryRemoteToDbMapper.map(category))

        db.taskDao().getAllByCategoryId(dbCategory.id).forEach {
            val updatedTask = it
            updatedTask.category_id = newCategoryId.toInt()
            db.taskDao().update(updatedTask)
        }
        db.categoryDao().delete(dbCategory)
    }

    private suspend fun cloudPostTask(dbTask: DbTask) {
        val task = api.postTask(TaskDbToRemotePostMapper.map(dbTask))
        db.taskDao().insertTask(TaskRemoteToDbMapper.map(task))
        db.taskDao().delete(dbTask)
    }

    private suspend fun cloudPatchTask(dbTask: DbTask) {
        val task = api.patchTask(dbTask.id, TaskDbToRemotePostMapper.map(dbTask))
        db.taskDao().update(TaskRemoteToDbMapper.map(task))
    }

    private suspend fun cloudDeleteTask(id: Int) = api.deleteTask(id)

//LOGS

    private suspend fun logAddTask(id: Int) {
        db.logDao().insert(DbLog(0, LogType.INSERT, LogModel.TASK, id))
    }

    private suspend fun logUpdateTask(id: Int) {
        val taskLog = db.logDao().getLogByIdModel(id, LogModel.TASK)
        if (taskLog == null) {
            db.logDao()
                .insert(DbLog(0, LogType.UPDATE, LogModel.TASK, id))
        }

    }

    private suspend fun logDeleteTask(id: Int) {
        val taskLog = db.logDao().getLogByIdModel(id, LogModel.TASK)
        if (taskLog != null) {
            when (taskLog.logType) {
                LogType.INSERT -> db.logDao().delete(taskLog)
                LogType.UPDATE -> {
                    db.logDao().delete(taskLog)
                    db.logDao().insert(DbLog(0,
                        LogType.DELETE,
                        LogModel.TASK,
                        id))
                }
            }
        } else {
            db.logDao()
                .insert(DbLog(0, LogType.DELETE, LogModel.TASK, id))
        }
    }

    private suspend fun logAddCategory(id: Int) {
        db.logDao()
            .insert(DbLog(0, LogType.INSERT, LogModel.CATEGORY, id))
    }

}
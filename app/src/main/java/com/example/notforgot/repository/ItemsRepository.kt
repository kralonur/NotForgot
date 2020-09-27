package com.example.notforgot.repository

import android.content.Context
import com.example.notforgot.api.NetworkService
import com.example.notforgot.database.AppDatabase
import com.example.notforgot.model.ResultWrapper
import com.example.notforgot.model.db.DbLog
import com.example.notforgot.model.db.items.DbCategory
import com.example.notforgot.model.db.items.DbPriority
import com.example.notforgot.model.db.items.DbTask
import com.example.notforgot.model.items.category.CategoryPost
import com.example.notforgot.model.items.task.TaskPost
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber

class ItemsRepository(context: Context) {
    private val api = NetworkService.getItemsService(context)
    private val db = AppDatabase.getInstance(context)

    fun fetchFromCloud() = flow {
        emit(ResultWrapper.Loading)
        val categories = api.getCategories().map { DbCategory(it.id, it.name) }
        val priorities = api.getPriorities().map { DbPriority(it.id, it.name, it.color) }
        val tasks = api.getTasks().map {
            DbTask(
                it.id,
                it.title,
                it.description,
                it.done,
                it.created,
                it.deadline,
                it.category.id,
                it.priority.id
            )
        }

        db.categoryDao().insertAll(categories)
        db.priorityDao().insertAll(priorities)
        db.taskDao().insertAll(tasks)

        emit(ResultWrapper.Success(Any()))
    }.catch {
        Timber.e(it)
        emit(ResultWrapper.Error)
    }

    fun uploadToCloud() = flow {
        emit(ResultWrapper.Loading)
        uploadCategories()
        uploadTasks()

        emit(ResultWrapper.Success((Any())))
    }.catch {
        Timber.e(it)
        emit(ResultWrapper.Error)
    }


    fun addCategory(category: DbCategory) = flow {
        emit(ResultWrapper.Loading)
        val returnVal = db.categoryDao().insertCategory(category)
        Timber.i(returnVal.toString())
        logAddCategory(returnVal.toInt())
        emit(ResultWrapper.Success(returnVal))
    }.catch {
        Timber.e(it)
        emit(ResultWrapper.Error)
    }

    fun addTask(task: DbTask) = flow {
        emit(ResultWrapper.Loading)
        val returnVal = db.taskDao().insertTask(task)
        Timber.i(returnVal.toString())
        logAddTask(returnVal.toInt())
        emit(ResultWrapper.Success(returnVal))
    }.catch {
        Timber.e(it)
        emit(ResultWrapper.Error)
    }

    fun updateTask(task: DbTask) = flow {
        emit(ResultWrapper.Loading)
        val returnVal = db.taskDao().update(task)
        logUpdateTask(task.id)
        emit(ResultWrapper.Success(returnVal))
    }.catch {
        Timber.e(it)
        emit(ResultWrapper.Error)
    }

    fun deleteTask(task: DbTask) = flow {
        emit(ResultWrapper.Loading)
        val returnVal = db.taskDao().delete(task)
        logDeleteTask(task.id)
        emit(ResultWrapper.Success(returnVal))
    }.catch {
        Timber.e(it)
        emit(ResultWrapper.Error)
    }

    fun getTask(id: Int) = flow {
        emit(ResultWrapper.Loading)
        val returnVal = db.taskDao().getTaskById(id)
        emit(ResultWrapper.Success(returnVal))
    }.catch {
        Timber.e(it)
        emit(ResultWrapper.Error)
    }

    fun getTaskDomain(id: Int) = db.taskDao().getTaskDomainById(id)

    fun getCategory(id: Int) = flow {
        emit(ResultWrapper.Loading)
        val returnVal = db.categoryDao().getCategoryById(id)
        emit(ResultWrapper.Success(returnVal))
    }.catch {
        Timber.e(it)
        emit(ResultWrapper.Error)
    }

    fun getPriority(id: Int) = flow {
        emit(ResultWrapper.Loading)
        val returnVal = db.priorityDao().getPriorityById(id)
        emit(ResultWrapper.Success(returnVal))
    }.catch {
        Timber.e(it)
        emit(ResultWrapper.Error)
    }

    fun getTaskList() = db.taskDao().getAll()

    fun getTaskDomainList() = db.taskDao().getAllDomain()

    fun getCategoryList() = db.categoryDao().getAll()

    fun getPriorityList() = db.priorityDao().getAll()

    //CLOUD

    private suspend fun uploadCategories() {
        db.logDao().getAllCategory().forEach {
            val category = db.categoryDao().getCategoryById(it.model_id)
            when (it.type) {
                "INSERT" -> cloudPostCategory(category)
            }
            db.logDao().delete(it)
        }
    }

    private suspend fun uploadTasks() {
        db.logDao().getAllTask().forEach {
            val task = db.taskDao().getTaskById(it.model_id)
            when (it.type) {
                "INSERT" -> cloudPostTask(task)
                "UPDATE" -> cloudPatchTask(task)
                "DELETE" -> cloudDeleteTask(task)
            }
            db.logDao().delete(it)
        }
    }

    private suspend fun cloudPostCategory(dbCategory: DbCategory) {
        val category = api.postCategory(CategoryPost(dbCategory.name))
        db.categoryDao().delete(dbCategory)
        val newCategoryId = db.categoryDao().insertCategory(DbCategory(category.id, category.name))

        db.taskDao().getAllByCategoryId(dbCategory.id).forEach {
            val updatedTask = it
            updatedTask.category_id = newCategoryId.toInt()
            db.taskDao().update(updatedTask)
        }
    }

    private suspend fun cloudPostTask(dbTask: DbTask) {
        val task = api.postTask(TaskPost(
            dbTask.title,
            dbTask.description,
            dbTask.done,
            dbTask.deadline,
            dbTask.category_id,
            dbTask.priority_id
        ))
        db.taskDao().delete(dbTask)
        db.taskDao().insertTask(DbTask(
            task.id,
            task.title,
            task.description,
            task.done,
            task.created,
            task.deadline,
            task.category.id,
            task.priority.id
        ))
    }

    private suspend fun cloudPatchTask(dbTask: DbTask) {
        val task = api.patchTask(dbTask.id, TaskPost(
            dbTask.title,
            dbTask.description,
            dbTask.done,
            dbTask.deadline,
            dbTask.category_id,
            dbTask.priority_id
        ))
        db.taskDao().update(DbTask(
            task.id,
            task.title,
            task.description,
            task.done,
            task.created,
            task.deadline,
            task.category.id,
            task.priority.id
        ))
    }

    private suspend fun cloudDeleteTask(dbTask: DbTask) {
        api.deleteTask(dbTask.id)
        db.taskDao().delete(dbTask)
    }

    //LOGS

    private suspend fun logAddTask(id: Int) {
        db.logDao().insert(DbLog(0, "INSERT", "TASK", id))
    }

    private suspend fun logUpdateTask(id: Int) {
        val taskLog = db.logDao().getTaskLogById(id)
        if (taskLog != null) {
            when (taskLog.type) {
                "INSERT", "UPDATE", "DELETE" -> {
                }
            }
        } else {
            db.logDao().insert(DbLog(0, "UPDATE", "TASK", id))
        }

    }

    private suspend fun logDeleteTask(id: Int) {
        val taskLog = db.logDao().getTaskLogById(id)
        if (taskLog != null) {
            when (taskLog.type) {
                "INSERT" -> db.logDao().delete(taskLog)
                "UPDATE" -> {
                    db.logDao().delete(taskLog)
                    db.logDao().insert(DbLog(0, "DELETE", "TASK", id))
                }
                "DELETE" -> {
                }
            }
        } else {
            db.logDao().insert(DbLog(0, "DELETE", "TASK", id))
        }
    }

    private suspend fun logAddCategory(id: Int) {
        db.logDao().insert(DbLog(0, "INSERT", "CATEGORY", id))
    }

}
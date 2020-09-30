package com.example.notforgot.repository

import android.content.Context
import com.example.notforgot.api.NetworkService
import com.example.notforgot.database.AppDatabase
import com.example.notforgot.model.RecviewItem
import com.example.notforgot.model.db.DbLog
import com.example.notforgot.model.db.items.DbCategory
import com.example.notforgot.model.db.items.DbPriority
import com.example.notforgot.model.db.items.DbTask
import com.example.notforgot.model.items.category.CategoryPost
import com.example.notforgot.model.items.task.TaskPost
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class ItemsRepository(context: Context) : BaseRepository() {
    private val api = NetworkService.getItemsService(context)
    private val db = AppDatabase.getInstance(context)

    fun fetchFromCloud() = flowCall {
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
        db.taskDao().getAllDomain().map { it.groupBy { item -> item.category } }.map { map ->
            val list = ArrayList<RecviewItem>()
            map.keys.forEach { k ->
                list.add(RecviewItem(false, category = k))
                map[k]?.forEach { v ->
                    list.add(RecviewItem(true, task = v))
                }
            }
            return@map list
        }

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
                "DELETE" -> cloudDeleteTask(it.model_id)
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

    private suspend fun cloudDeleteTask(id: Int) = api.deleteTask(id)

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
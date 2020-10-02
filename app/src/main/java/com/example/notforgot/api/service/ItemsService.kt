package com.example.notforgot.api.service

import com.example.notforgot.model.remote.items.category.Category
import com.example.notforgot.model.remote.items.category.CategoryPost
import com.example.notforgot.model.remote.items.priority.Priority
import com.example.notforgot.model.remote.items.task.Task
import com.example.notforgot.model.remote.items.task.TaskPost
import com.example.notforgot.util.Constants
import retrofit2.http.*

interface ItemsService {

    @GET(Constants.Api.PRIORITIES)
    suspend fun getPriorities(): List<Priority>

    @GET(Constants.Api.CATEGORIES)
    suspend fun getCategories(): List<Category>

    @POST(Constants.Api.CATEGORIES)
    suspend fun postCategory(
        @Body category: CategoryPost,
    ): Category

    @GET(Constants.Api.TASKS)
    suspend fun getTasks(): List<Task>

    @POST(Constants.Api.TASKS)
    suspend fun postTask(
        @Body task: TaskPost,
    ): Task

    @PATCH("${Constants.Api.TASKS}/{id}")
    suspend fun patchTask(
        @Path("id") id: Int,
        @Body task: TaskPost,
    ): Task

    @DELETE("${Constants.Api.TASKS}/{id}")
    suspend fun deleteTask(
        @Path("id") id: Int,
    )
}
package com.bignerdranch.android.todolist.Repository

import com.bignerdranch.android.todolist.database.TaskDao
import com.bignerdranch.android.todolist.model.Task
import kotlinx.coroutines.flow.Flow
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "task-database"

object TaskRepository {

    lateinit var taskDao: TaskDao

    private val executor = Executors.newSingleThreadExecutor()

    fun getTasks(): Flow<List<Task>> = taskDao.getTasks()
    fun getTask(id: UUID): Flow<Task?> = taskDao.getTask(id)

    suspend fun updateTask(task: Task){
        taskDao.updateTask(task)
    }

    suspend fun addTask(task: Task){
        taskDao.insertTask(task)
    }

    suspend fun deleteTask(task: Task){
        taskDao.deleteTask(task)
    }

}
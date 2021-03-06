package com.bignerdranch.android.todolist.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.bignerdranch.android.todolist.Task
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "task-database"

class TaskRepository private constructor(context: Context){

    private val database: TaskDatabase = Room.databaseBuilder(
        context.applicationContext,
        TaskDatabase::class.java,
        DATABASE_NAME
    ).build()

    private val taskDao = database.taskDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getTasks(): LiveData<List<Task>> = taskDao.getTasks()
    fun getTask(id: UUID): LiveData<Task?> = taskDao.getTask(id)

    fun updateTask(task: Task){
        executor.execute {
            taskDao.updateTask(task)
        }
    }

    fun addTask(task: Task){
        executor.execute {
            taskDao.insertTask(task)
        }
    }

    fun deleteTask(task: Task){
        executor.execute {
            taskDao.deleteTask(task)
        }
    }

    companion object {
        private var INSTANCE: TaskRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) INSTANCE = TaskRepository(context)
        }

        fun get(): TaskRepository {
            return INSTANCE?: throw IllegalStateException("The Repo is not init yet")
        }
    }

}
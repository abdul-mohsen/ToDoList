package com.bignerdranch.android.todolist

import android.app.Application
import com.bignerdranch.android.todolist.Repository.TaskRepository
import com.bignerdranch.android.todolist.database.AppDatabase
import timber.log.Timber

class ToDoListApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        AppDatabase.invoke(this).also {
            TaskRepository.taskDao = it.taskDao()
        }
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }
}
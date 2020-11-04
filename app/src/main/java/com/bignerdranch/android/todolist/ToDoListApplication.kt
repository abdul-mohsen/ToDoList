package com.bignerdranch.android.todolist

import android.app.Application
import com.bignerdranch.android.todolist.database.TaskRepository

class ToDoListApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        TaskRepository.initialize(this)
    }
}
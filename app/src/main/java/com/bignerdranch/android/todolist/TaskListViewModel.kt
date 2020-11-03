package com.bignerdranch.android.todolist

import androidx.lifecycle.ViewModel
import com.bignerdranch.android.todolist.database.TaskRepository

class TaskListViewModel: ViewModel() {
    private val taskRepository = TaskRepository.get()
    val taskLiveDate = taskRepository.getTasks()

    fun addTask(task: Task){
        taskRepository.addTask(task)
    }
}
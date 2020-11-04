package com.bignerdranch.android.todolist

import androidx.lifecycle.ViewModel
import com.bignerdranch.android.todolist.database.TaskRepository
import java.util.*

class TaskListViewModel: ViewModel() {
    private val taskRepository = TaskRepository.get()
    val taskLiveDate = taskRepository.getTasks()

    fun addTask(task: Task){
        taskRepository.addTask(task)
    }

    fun updateTasks(tasks: Map<UUID, Task?>){
        if (tasks.isEmpty()) return
        tasks.forEach{(key, value) ->
            if (value == null) {
                taskRepository.deleteTask(Task(id = key))
            } else{
                taskRepository.updateTask(value)
            }
        }
    }

    fun deleteTask(task: Task){
        taskRepository.deleteTask(task)
    }
}
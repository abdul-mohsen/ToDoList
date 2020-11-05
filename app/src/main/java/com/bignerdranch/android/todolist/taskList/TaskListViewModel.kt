package com.bignerdranch.android.todolist.taskList

import androidx.lifecycle.ViewModel
import com.bignerdranch.android.todolist.ItemState
import com.bignerdranch.android.todolist.classes.Task
import com.bignerdranch.android.todolist.database.TaskRepository
import java.util.*

class TaskListViewModel: ViewModel() {
    private val taskRepository = TaskRepository.get()
    val taskLiveDate = taskRepository.getTasks()

    fun addTask(task: Task){
        taskRepository.addTask(task)
    }

    fun updateTasks(tasks: Map<UUID, Pair<ItemState,Task>>){
        if (tasks.isEmpty()) return
        tasks.forEach{(_, value) -> when(value.first){
            ItemState.Add-> taskRepository.addTask(value.second)
            ItemState.Update-> taskRepository.updateTask(value.second)
            ItemState.Delete-> taskRepository.deleteTask(value.second)
        }
        }
    }

    fun deleteTask(task: Task){
        taskRepository.deleteTask(task)
    }
}
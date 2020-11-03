package com.bignerdranch.android.todolist

import androidx.lifecycle.*
import com.bignerdranch.android.todolist.database.TaskRepository
import java.util.*

class AddTaskViewModel: ViewModel() {
    private val taskIdLiveData = MutableLiveData<UUID>()
    private val taskRepository = TaskRepository.get()

    val taskLiveData: LiveData<Task?> = taskIdLiveData.switchMap { id ->
        taskRepository.getTask(id)
    }

    fun loadTask(taskId: UUID){
        taskIdLiveData.value = taskId
    }

    fun updateTask(task: Task) {
        taskRepository.updateTask(task)
    }

}
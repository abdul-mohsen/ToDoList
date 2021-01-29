package com.bignerdranch.android.todolist.view.addTask

import androidx.lifecycle.*
import com.bignerdranch.android.todolist.Repository.TaskRepository
import com.bignerdranch.android.todolist.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class AddTaskViewModel: ViewModel() {
    private val _task: MutableStateFlow<Task?> = MutableStateFlow(null)
    val task: StateFlow<Task?> = _task
    private var taskJob: Job? = null

    init {
        Timber.d("new view model")
    }

    fun getTaskData(id: String?) {
        val uuid = if (id != null) UUID.fromString(id) else UUID.randomUUID()
        if (taskJob != null && uuid == task.value?.id) return
        taskJob = viewModelScope.launch(Dispatchers.IO) {
            TaskRepository.getTask(uuid).stateIn(viewModelScope).collect { task ->
                _task.emit(task)
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            TaskRepository.updateTask(task)
        }
    }

    fun addTask(task: Task){
        viewModelScope.launch(Dispatchers.IO) {
            TaskRepository.addTask(task)
        }
    }

    fun updateDate(time: Long) {
        viewModelScope.launch {
            if (task.value != null) {
                _task.emit(task.value!!.copy().also { it.date = Date(time) })
            }
        }
    }
}
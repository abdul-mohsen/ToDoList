package com.bignerdranch.android.todolist.view.taskList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.todolist.Repository.TaskRepository
import com.bignerdranch.android.todolist.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.*

class TaskListViewModel: ViewModel() {

    lateinit var tasks: StateFlow<List<Task>>

    init {
        viewModelScope.launch(Dispatchers.IO) {
            tasks = TaskRepository.getTasks().stateIn(viewModelScope)
        }
    }



    suspend fun getTasks(): StateFlow<List<Task>> {
        tasks = TaskRepository.getTasks().stateIn(viewModelScope)
        return tasks
    }

//    fun updateTasks(tasks: Map<UUID, Pair<ItemState,Task>>){
//        if (tasks.isEmpty()) return
//        tasks.forEach{(_, value) -> when(value.first) {
//            ItemState.Add-> taskRepository.addTask(value.second)
//            ItemState.Update-> taskRepository.updateTask(value.second)
//            ItemState.Delete-> taskRepository.deleteTask(value.second)
//        }
//        }
//    }

    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            TaskRepository.addTask(task)
        }
    }

    fun deleteTask(id: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            TaskRepository.deleteTask(Task(id = id))
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            TaskRepository.updateTask(task)
        }
    }
}
package com.bignerdranch.android.todolist.view.taskList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.todolist.Repository.TaskRepository
import com.bignerdranch.android.todolist.model.SortOptionStatus
import com.bignerdranch.android.todolist.model.Task
import com.bignerdranch.android.todolist.next
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class TaskListViewModel: ViewModel() {

    private val _tasks: MutableStateFlow<List<Task>> = MutableStateFlow(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _sortByState: MutableStateFlow<Map<String, SortOptionStatus>> =
        MutableStateFlow(mapOf("date" to SortOptionStatus.IDLE, "creation" to SortOptionStatus.IDLE))
    val sortByState: StateFlow<Map<String, SortOptionStatus>> = _sortByState

    private var taskJob: Job? = null

    fun <T : Comparable<T>> getTasks(
        item: String = "date",
        selector: (Task) -> T?
    ) {
        taskJob?.cancel()
        taskJob = viewModelScope.launch(Dispatchers.IO) {
            updateStateList(item)
            TaskRepository.getTasks().collect { list ->
                _tasks.emit(list.sortingOption(sortByState.value[item]?:SortOptionStatus.IDLE, selector))
            }
        }
    }

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

    fun updateStateList(item: String) {
        viewModelScope.launch {
            _sortByState.emit(sortByState.value.toMutableMap().also {
                for ( (key, value) in it) {
                    it[key] = if (key == item) value.next() else SortOptionStatus.IDLE
                }
            })
        }
    }

    private fun <T : Comparable<T>> Iterable<Task>.sortingOption(
        state: SortOptionStatus,
        selector: (Task) -> T?) = when (state) {
            SortOptionStatus.DES -> { sortedByDescending(selector) }
            SortOptionStatus.AES -> { sortedWith(compareBy(nullsLast(), selector)) }
            SortOptionStatus.IDLE -> { this.toList() }
        }
}

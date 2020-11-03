package com.bignerdranch.android.todolist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.*

class AddTaskViewModel: ViewModel() {
    val taskIdLiveData = MutableLiveData<UUID>()

    fun loadTask(taskId: UUID){
        taskIdLiveData.value = taskId
    }
}
package com.bignerdranch.android.todolist.taskList

import androidx.recyclerview.widget.DiffUtil
import com.bignerdranch.android.todolist.classes.Task

class TaskItemDiffCallback: DiffUtil.ItemCallback<Task>(){
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
        oldItem == newItem
}
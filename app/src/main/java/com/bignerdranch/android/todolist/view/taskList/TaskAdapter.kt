package com.bignerdranch.android.todolist.view.taskList

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.format.DateFormat
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.todolist.R
import com.bignerdranch.android.todolist.databinding.TaskItemBinding
import com.bignerdranch.android.todolist.getColorFromAttr
import com.bignerdranch.android.todolist.model.Status
import com.bignerdranch.android.todolist.model.Task
import java.util.*

class TaskAdapter(
    private val nav: (String) -> Unit,
    private val deleteItem: (UUID) -> Unit)
    : ListAdapter<Task, TaskAdapter.TaskHolder>(TaskItemDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = TaskItemBinding.inflate(layoutInflater, parent, false)
        return TaskHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class TaskHolder(
        private val bindingHolder: TaskItemBinding,
        private val context: Context
        ): RecyclerView.ViewHolder(bindingHolder.root) {

        fun bind(task: Task){
            val spannable = SpannableString(task.titile)
            bindingHolder.run {
                bindingHolder.root.setOnClickListener { nav(task.id.toString()) }
                deleteButton.setOnClickListener { deleteItem(task.id) }
                if (task.status == Status.Achieved){
                    spannable.setSpan(StrikethroughSpan(), 0, task.titile.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                    deleteButton.visibility = View.VISIBLE
                    textTitle.setTextColor(ContextCompat.getColor(context, R.color.strike))
                } else {
                    deleteButton.visibility = View.INVISIBLE
                    textTitle.setTextColor(context.getColorFromAttr(R.attr.colorOnSecondary))
                    task.status = updateState(task)
                }
                textTitle.text = spannable
                textDate.text =
                    if (task.date == null){ "Date" }
                    else { DateFormat.format(DATE_FORMAT, task.date) }
                textStatus.text = task.status.toString()
                textStatus.setTextColor(ContextCompat.getColor(context, getColor(task.status)))
            }
        }
    }
    private fun updateState(task: Task, state: Boolean = false): Status = when {
            state -> Status.Achieved
            task.date == null -> Status.SomeDay
            Date().after(task.date) -> Status.Overdue
            else -> Status.Upcoming
    }

    class TaskItemDiffCallback: DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem == newItem
    }

    fun getColor(state: Status) = when (state) {
        Status.Overdue -> R.color.red
        Status.Achieved -> R.color.green
        Status.InProgress -> R.color.yellow
        Status.Upcoming -> R.color.blue
        Status.SomeDay -> R.color.gray
    }

    companion object {
        private const val DATE_FORMAT = "EEEE dd/MM/yy"
    }
}


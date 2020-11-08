package com.bignerdranch.android.todolist.taskList

import android.content.Context
import android.content.res.Resources
import android.text.SpannableString
import android.text.Spanned
import android.text.format.DateFormat
import android.text.style.StrikethroughSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.todolist.ItemState
import com.bignerdranch.android.todolist.R
import com.bignerdranch.android.todolist.Status
import com.bignerdranch.android.todolist.classes.AutoUpdatableAdapter
import com.bignerdranch.android.todolist.classes.Task
import com.bignerdranch.android.todolist.getColorFromAttr
import java.util.*
import kotlin.properties.Delegates

private const val DATE_FORMAT = "EEEE dd/MM/yy"


class TaskAdapter(private val context: Context, private val taskListFragment: TaskListFragment)
    : ListAdapter<Task, TaskAdapter.TaskHolder>(TaskItemDiffCallback()),
    AutoUpdatableAdapter {

    var tasks: List<Task> by Delegates.observable(emptyList()) { _, oldList, newList ->
        autoNotify(oldList, newList) { o, n -> o.id == n.id }
    }

    val taskChangesMap:MutableMap<UUID, Pair<ItemState,Task>> = emptyMap<UUID, Pair<ItemState,Task>>().toMutableMap()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.task_item, parent, false)
        return TaskHolder(view)
    }

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        holder.bind(tasks[position])
    }
    override fun getItemCount(): Int = tasks.size

    fun removeWithId(id: UUID){
        val position = tasks.indexOf(tasks.first { task -> task.id == id })
        tasks = tasks.toMutableList().apply {
            removeAt(position)
            toList()
        }
    }

    fun addTask(task: Task){
        taskChangesMap[task.id] = Pair(ItemState.Add, task)
        tasks = tasks.toMutableList().apply {
            add(task)
            toList()
        }
    }

    fun loadTask(taskList: List<Task>){
        tasks = taskList
    }

    inner class TaskHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener{

        private lateinit var task: Task
        private val titleText: TextView = view.findViewById(R.id.text_title)
        private val tagText: TextView = view.findViewById(R.id.text_tag)
        private val dateText: TextView = view.findViewById(R.id.text_date)
        private val statusText: TextView = view.findViewById(R.id.text_status)
        private val deleteButton: ImageButton = view.findViewById(R.id.delete_button)

        init {
            itemView.setOnClickListener(this)

        deleteButton.setOnClickListener {
            taskChangesMap[task.id] = Pair(ItemState.Delete, task)
            removeWithId(task.id)
        }
        }

        fun bind(task: Task){
            this.task = task
            val spannable = SpannableString(task.titile)


            if (task.status.ordinal == 0){
                spannable.setSpan(StrikethroughSpan(), 0, task.titile.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                deleteButton.visibility = View.VISIBLE
                titleText.setTextColor(ContextCompat.getColor(context, R.color.strike))


            } else {
                deleteButton.visibility = View.INVISIBLE
                titleText.setTextColor(context.getColorFromAttr(R.attr.colorOnSecondary))
                task.status = updateState(task)

            }
            titleText.text = spannable
            dateText.text = if (task.date == null){
                "Date"
            } else{
                DateFormat.format(DATE_FORMAT, task.date)
            }
            statusText.text = task.status.toString()
             val cr= when(task.status){
                Status.Overdue -> R.color.red
                Status.Achieved -> R.color.green
                Status.InProgress -> R.color.yellow
                Status.Upcoming -> R.color.blue
                Status.SomeDay -> R.color.gray
            }
            statusText.setTextColor(ContextCompat.getColor(context, cr))
        }

        override fun onClick(v: View?) {
            NavHostFragment.findNavController(taskListFragment).navigate(
                TaskListFragmentDirections.actionTaskListFragmentToAddTask(task.id)
            )
        }
    }

    fun update(adapterPosition: Int, state: Boolean = false){
        tasks[adapterPosition].status = updateState(tasks[adapterPosition], state)
        taskChangesMap[tasks[adapterPosition].id] = Pair(ItemState.Update, tasks[adapterPosition])
        notifyItemChanged(adapterPosition)
    }

    private fun updateState(task: Task, state: Boolean = false): Status = when {
            state -> Status.Achieved
            task.date == null -> Status.SomeDay
            Date().after(task.date) -> Status.Overdue
            else -> Status.Upcoming

    }


}


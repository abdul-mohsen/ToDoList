package com.bignerdranch.android.todolist

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.*
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.*
import androidx.core.text.toSpannable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.navArgument
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class TaskListFragment:Fragment() {

    private lateinit var taskRecyclerView: RecyclerView
    private var taskAdapter: TaskAdapter? = TaskAdapter(emptyList())
    private val taskListViewModel: TaskListViewModel by lazy {
        ViewModelProvider(this).get(TaskListViewModel::class.java)
    }
    private val taskChangesMap:MutableMap<UUID, Task?> = emptyMap<UUID, Task>().toMutableMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)

        taskRecyclerView = view.findViewById(R.id.task_list)
        taskRecyclerView.adapter = taskAdapter

        view.findViewById<FloatingActionButton>(R.id.add_task).setOnClickListener {
            val task = Task()
            taskListViewModel.addTask(task)
            findNavController().navigate(
                TaskListFragmentDirections.actionTaskListFragmentToAddTask(task.id)
            )
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_task_list, menu)
    }

    override fun onStart() {
        super.onStart()
        taskListViewModel.taskLiveDate.observe(
            viewLifecycleOwner,
            {
                it?.let {
                    updateUI(it)
                }
            }
        )
    }

    override fun onStop() {
        super.onStop()
        taskListViewModel.updateTasks(taskChangesMap)
        taskChangesMap.clear()
    }

    private fun updateUI(tasks: List<Task>){
        taskAdapter?.let {
            it.tasks = tasks
        } ?: run {
            taskAdapter = TaskAdapter(tasks)
        }
        taskRecyclerView.adapter = taskAdapter
    }

    private inner class TaskHolder(view:View): RecyclerView.ViewHolder(view), View.OnClickListener{

        private lateinit var task: Task
        private val isTaskDoneCheckBox: CheckBox = view.findViewById(R.id.is_task_done)
        private val titleText: TextView = view.findViewById(R.id.text_title)
        private val tagText: TextView = view.findViewById(R.id.text_tag)
        private val dateText: TextView = view.findViewById(R.id.text_date)
        private val statusText: TextView = view.findViewById(R.id.text_status)
        private val deleteButton: ImageButton = view.findViewById(R.id.delete_button)

        init {
            view.setOnClickListener(this)
            isTaskDoneCheckBox.apply {
                setOnClickListener {
                    val spannable = SpannableString(task.titile)
                    titleText.text = spannable
                    if (this.isChecked){
                        task.status = Status.Done
                        spannable.setSpan(StrikethroughSpan(), 0, task.titile.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                        deleteButton.visibility = VISIBLE
                    }
                    else {
                        task.status = Status.InProgress
                        deleteButton.visibility = INVISIBLE
                    }
                    taskChangesMap[task.id] = task
                    titleText.text = spannable
                }
            }

            deleteButton.setOnClickListener {
                taskChangesMap[task.id] = null
                taskAdapter?.removeWithId(task.id)
                taskRecyclerView.adapter = taskAdapter
            }
        }

        fun bind(task: Task){
            this.task = task
            val spannable = SpannableString(task.titile)

            if (task.status.ordinal == 0){
                isTaskDoneCheckBox.isChecked = true
                spannable.setSpan(StrikethroughSpan(), 0, task.titile.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                deleteButton.visibility = VISIBLE
            }
            titleText.text = spannable
            dateText.text = task.date?.toString()?:""
            statusText.text = task.status.toString()

        }

        override fun onClick(v: View?) {
            findNavController().navigate(
                TaskListFragmentDirections.actionTaskListFragmentToAddTask(task.id)
            )
        }
    }

    private inner class TaskAdapter(var tasks: List<Task>): RecyclerView.Adapter<TaskHolder>(){
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
            taskListViewModel.deleteTask(tasks[position])
            tasks = tasks.toMutableList().apply {
                removeAt(position)
                toList()
            }
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, tasks.size)
        }

    }
}
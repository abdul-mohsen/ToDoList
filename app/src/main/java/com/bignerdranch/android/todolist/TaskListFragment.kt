package com.bignerdranch.android.todolist

import android.graphics.Canvas
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.format.DateFormat
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.*
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

private const val DATE_FORMAT = "EEEE dd/MM/yy"

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
        val callback = taskAdapter?.let {
            DragManagerAdapter(it,
                ItemTouchHelper.ACTION_STATE_IDLE,
                ItemTouchHelper.RIGHT.or(ItemTouchHelper.LEFT))}
        val helper = callback?.let { ItemTouchHelper(it) }
        helper?.attachToRecyclerView(taskRecyclerView)


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
        private val titleText: TextView = view.findViewById(R.id.text_title)
        private val tagText: TextView = view.findViewById(R.id.text_tag)
        private val dateText: TextView = view.findViewById(R.id.text_date)
        private val statusText: TextView = view.findViewById(R.id.text_status)
        private val deleteButton: ImageButton = view.findViewById(R.id.delete_button)

        init {
            itemView.setOnClickListener(this)

            deleteButton.setOnClickListener {
                taskChangesMap[task.id] = null
                (taskRecyclerView.adapter as TaskAdapter).removeWithId(task.id)
            }
        }

        fun bind(task: Task){
            this.task = task
            val spannable = SpannableString(task.titile)

            if (task.status.ordinal == 0){
                spannable.setSpan(StrikethroughSpan(), 0, task.titile.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                deleteButton.visibility = VISIBLE
                titleText.setTextColor(ContextCompat.getColor(requireContext(),R.color.light_gray))
            } else {
                deleteButton.visibility = INVISIBLE
                titleText.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
            }
            titleText.text = spannable
            dateText.text = getString(R.string.date)
            task.date?.let {
                dateText.text = DateFormat.format(DATE_FORMAT, it)
            }
            statusText.text = task.status.toString()
        }

        fun changeState(state:Boolean = false) {
            task.status = if (state) Status.Done
            else Status.InProgress
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
            tasks = tasks.toMutableList().apply {
                removeAt(position)
                toList()
            }
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, tasks.size)
        }

    }

    private class DragManagerAdapter(
        val adapter: TaskAdapter,
        dragDirs: Int,
        swipeDirs: Int)
        :ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs){

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean = true
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            when (direction) {
                ItemTouchHelper.RIGHT -> (viewHolder as TaskHolder).changeState(true)
                ItemTouchHelper.LEFT -> (viewHolder as TaskHolder).changeState(false)
                else -> Log.d("Testing", "hmmm")
            }
            adapter.notifyItemChanged(viewHolder.adapterPosition)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
        }

        override fun getAnimationDuration(
            recyclerView: RecyclerView,
            animationType: Int,
            animateDx: Float,
            animateDy: Float
        ): Long = 0

        override fun getSwipeVelocityThreshold(defaultValue: Float): Float = 10F
        override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 0.3F
    }
}
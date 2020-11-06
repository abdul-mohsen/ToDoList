package com.bignerdranch.android.todolist.taskList

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
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ListAdapter
import com.bignerdranch.android.todolist.*
import com.bignerdranch.android.todolist.classes.AutoUpdatableAdapter
import com.bignerdranch.android.todolist.classes.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import kotlin.properties.Delegates

private const val DATE_FORMAT = "EEEE dd/MM/yy"

class TaskListFragment:Fragment() {

    private lateinit var menu:Menu
    private lateinit var newTaskEdit:EditText
    private lateinit var addTaskButton: FloatingActionButton
    private lateinit var taskRecyclerView: RecyclerView
    private var taskAdapter: TaskAdapter = TaskAdapter()
    private val taskListViewModel: TaskListViewModel by lazy {
        ViewModelProvider(this).get(TaskListViewModel::class.java)
    }
    private val taskChangesMap:MutableMap<UUID, Pair<ItemState,Task>> = emptyMap<UUID, Pair<ItemState,Task>>().toMutableMap()
    private val sortByState :MutableList<SortOptionStatus> =
        listOf(SortOptionStatus.IDLE, SortOptionStatus.IDLE).toMutableList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)
        taskAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        taskRecyclerView = view.findViewById(R.id.task_list)
        taskRecyclerView.adapter = taskAdapter
        val callback = DragManagerAdapter(
            taskAdapter,
            ItemTouchHelper.ACTION_STATE_IDLE,
            ItemTouchHelper.RIGHT.or(ItemTouchHelper.LEFT))
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(taskRecyclerView)

        newTaskEdit = view.findViewById(R.id.new_task_edit)
        addTaskButton = view.findViewById(R.id.add_task)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_task_list, menu)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        R.id.due_data_option -> {
            sortByState[0] = sortByState[0].next()
            when (sortByState[0]) {
                SortOptionStatus.DES -> {
                    taskAdapter.tasks.let { tasks ->
                        sortByState[1] = SortOptionStatus.IDLE
                        menu.findItem(R.id.creation_data_option).setIcon(0)
                        taskAdapter.loadTask(tasks.sortedByDescending { it.date })
                        item.setIcon(R.drawable.ic_double_arrow_down)
                    }
                    true
                }
                SortOptionStatus.AES -> {
                    taskAdapter.tasks.let { tasks ->
                        taskAdapter.loadTask( tasks.sortedWith(compareBy(nullsLast()){it.date}))
                        item.setIcon(R.drawable.ic_double_arrow_up)
                    }
                    true
                }
                SortOptionStatus.IDLE -> {
                    item.setIcon(0)
                    true
                }
            }
        }
        R.id.creation_data_option -> {
            sortByState[1] = sortByState[1].next()
            when (sortByState[1]) {
                SortOptionStatus.DES -> {
                    taskAdapter.tasks.let { tasks ->
                        sortByState[0] = SortOptionStatus.IDLE
                        menu.findItem(R.id.due_data_option).setIcon(0)
                        taskAdapter.loadTask(tasks.sortedByDescending { it.creationDate })
                        item.setIcon(R.drawable.ic_double_arrow_down)
                    }
                true
            }
            SortOptionStatus.AES -> {
                taskAdapter.tasks.let { tasks ->
                    taskAdapter.loadTask( tasks.sortedWith(compareBy(nullsLast()){it.creationDate}))
                    item.setIcon(R.drawable.ic_double_arrow_up)
                }
                true
            }
            SortOptionStatus.IDLE -> {
                item.setIcon(0)
                true
            }
        }
        }
        R.id.setting_option -> true
        else -> super.onOptionsItemSelected(item)
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

        addTaskButton.setOnClickListener {
                val taskTitle = newTaskEdit.text.toString()
                if (taskTitle.isBlank()) {
                    val task = Task()
                    taskListViewModel.addTask(task)
                    findNavController().navigate(
                        TaskListFragmentDirections.actionTaskListFragmentToAddTask(task.id)
                    )
                } else {
                    val task = Task(titile = taskTitle)
                    taskChangesMap[task.id] = Pair(ItemState.Add, task)
                    taskAdapter.addTask(task)
                    newTaskEdit.setText("")
                    taskRecyclerView.smoothScrollToPosition(taskAdapter.tasks.size - 1)
                }
            }

        newTaskEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val taskTitle = newTaskEdit.text.toString()
                if (!taskTitle.isBlank()) {
                    val task = Task(titile = taskTitle)
                    taskChangesMap[task.id] = Pair(ItemState.Add, task)
                    taskAdapter.addTask(task)
                    newTaskEdit.setText("")
                    taskRecyclerView.smoothScrollToPosition(taskAdapter.tasks.size - 1)
                }
            }
            false
        }
    }

    override fun onStop() {
        super.onStop()
        taskListViewModel.updateTasks(taskChangesMap)
        taskChangesMap.clear()
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
                taskChangesMap[task.id] = Pair(ItemState.Delete, task)
                taskAdapter.removeWithId(task.id)
            }
        }

        fun bind(task: Task){
            this.task = task
            val spannable = SpannableString(task.titile)

            if (task.status.ordinal == 0){
                spannable.setSpan(StrikethroughSpan(), 0, task.titile.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                deleteButton.visibility = VISIBLE
                titleText.setTextColor(ContextCompat.getColor(requireContext(), R.color.light_gray))
            } else {
                changeState()
                deleteButton.visibility = INVISIBLE
                titleText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            }
            titleText.text = spannable
            dateText.text = getString(R.string.date)
            task.date?.let {
                dateText.text = DateFormat.format(DATE_FORMAT, it)
            }
            statusText.text = task.status.toString()
        }

        fun changeState(state:Boolean = false) {
            task.status = if (state) Status.Achieved
            else if (task.date == null) Status.SomeDay
            else if(Date().after(task.date)) Status.Overdue
            else Status.InProgress
        }

        override fun onClick(v: View?) {
            findNavController().navigate(
                TaskListFragmentDirections.actionTaskListFragmentToAddTask(task.id)
            )
        }
    }

    private inner class TaskAdapter
        : ListAdapter<Task, TaskHolder>(TaskItemDiffCallback()),
        AutoUpdatableAdapter {

        var tasks: List<Task> by Delegates.observable(emptyList()) { _, oldList, newList ->
            autoNotify(oldList, newList) { o, n -> o.id == n.id }
        }

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
            tasks = tasks.toMutableList().apply {
                add(task)
                toList()
            }
        }

        fun loadTask(taskList: List<Task>){
            tasks = taskList
        }
    }
    private class TaskItemDiffCallback: DiffUtil.ItemCallback<Task>(){
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
            oldItem == newItem
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



    private fun updateUI(tasks: List<Task>, goDown:Boolean = false){
        taskAdapter.loadTask(tasks)
    }
}
package com.bignerdranch.android.todolist.taskList

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.todolist.R
import com.bignerdranch.android.todolist.SortOptionStatus
import com.bignerdranch.android.todolist.classes.Task
import com.bignerdranch.android.todolist.next
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TaskListFragment : Fragment() {

    private lateinit var menu: Menu
    private lateinit var newTaskEdit: EditText
    private lateinit var addTaskButton: FloatingActionButton
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private val taskListViewModel: TaskListViewModel by lazy {
        ViewModelProvider(this).get(TaskListViewModel::class.java)
    }
    private val sortByState: MutableList<SortOptionStatus> =
            listOf(SortOptionStatus.IDLE, SortOptionStatus.IDLE).toMutableList()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)
        taskAdapter = TaskAdapter(requireContext(), this)
        taskAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        taskRecyclerView = view.findViewById(R.id.task_list)
        taskRecyclerView.adapter = taskAdapter
        taskRecyclerView.addItemDecoration(
                SimpleDividerItemDecoration(Color.DKGRAY, 0.5, context))
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.due_data_option -> {
            sortByState[0] = sortByState[0].next()
            sortByState[1] = SortOptionStatus.IDLE
            menu.findItem(R.id.creation_data_option).setIcon(0)
            taskAdapter.tasks.sortingOption(item, sortByState[0]) { it.date }
            true
        }
        R.id.creation_data_option -> {
            sortByState[1] = sortByState[1].next()
            sortByState[0] = SortOptionStatus.IDLE
            menu.findItem(R.id.due_data_option).setIcon(0)
            taskAdapter.tasks.sortingOption(item, sortByState[1]) { it.creationDate }
            true
        }
        R.id.setting_option -> {
            findNavController().navigate(
                    TaskListFragmentDirections.actionTaskListFragmentToSettingsFragment())
            true
        }
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
                findNavController().navigate(
                        TaskListFragmentDirections.actionTaskListFragmentToAddTask()
                )
            } else {
                quickTask(taskTitle)
            }
        }

        newTaskEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val taskTitle = newTaskEdit.text.toString()
                if (!taskTitle.isBlank()) {
                    quickTask(taskTitle)
                }
            }
            false
        }
    }

    private fun quickTask(taskTitle: String) {
        val task = Task(titile = taskTitle)
        taskAdapter.addTask(task)
        newTaskEdit.setText("")
        taskRecyclerView.smoothScrollToPosition(taskAdapter.tasks.size - 1)
    }

    override fun onStop() {
        super.onStop()
        taskListViewModel.updateTasks(taskAdapter.taskChangesMap)
        taskAdapter.taskChangesMap.clear()
    }

    private fun updateUI(tasks: List<Task>) {
        taskAdapter.loadTask(tasks)
    }

    private fun <T : Comparable<T>> Iterable<Task>.sortingOption(
            item: MenuItem,
            state: SortOptionStatus,
            selector: (Task) -> T?) {
        when (state) {
            SortOptionStatus.DES -> {
                item.setIcon(R.drawable.ic_double_arrow_down)
                taskAdapter.loadTask(sortedByDescending(selector))
            }
            SortOptionStatus.AES -> {
                item.setIcon(R.drawable.ic_double_arrow_up)
                taskAdapter.loadTask(sortedWith(compareBy(nullsLast(), selector)))
            }
            SortOptionStatus.IDLE -> {
                item.setIcon(0)
            }
        }
    }
}
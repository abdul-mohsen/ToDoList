package com.bignerdranch.android.todolist.view.taskList

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.bignerdranch.android.todolist.R
import com.bignerdranch.android.todolist.databinding.FragmentTaskListBinding
import com.bignerdranch.android.todolist.model.SortOptionStatus
import com.bignerdranch.android.todolist.model.Status
import com.bignerdranch.android.todolist.model.Task
import com.bignerdranch.android.todolist.next
import com.bignerdranch.android.todolist.view.interfaces.INavigation
import kotlinx.coroutines.flow.collect
import timber.log.Timber

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!
    private lateinit var changeFragment: INavigation

    private lateinit var menu: Menu
    private lateinit var taskAdapter: TaskAdapter
    private val taskListViewModel: TaskListViewModel by lazy {
        ViewModelProvider(this).get(TaskListViewModel::class.java)
    }
    private val sortByState: MutableList<SortOptionStatus> =
            listOf(SortOptionStatus.IDLE, SortOptionStatus.IDLE).toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("new creation")
        changeFragment = requireActivity() as INavigation
        val x = { id: String -> changeFragment.replaceFragment(id) }
        taskAdapter = TaskAdapter(x) { id ->
            taskListViewModel.deleteTask(id)
        }
        taskListViewModel.getTasks { it.date }
        observeTaskList()
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        Timber.d("new view creation")
        (activity as AppCompatActivity).supportActionBar?.show()
        setHasOptionsMenu(true)
        if (_binding == null)
            _binding = FragmentTaskListBinding.inflate(inflater, container, false)

        val callback = DragManagerAdapter(
                ItemTouchHelper.ACTION_STATE_IDLE,
                ItemTouchHelper.RIGHT.or(ItemTouchHelper.LEFT)) { position, isDone ->
            val task = taskListViewModel.tasks.value[position].also {
                it.status = if (isDone) Status.Achieved
                else Status.InProgress
            }
            taskListViewModel.updateTask(task)
        }
        val helper = ItemTouchHelper(callback)

        binding.taskList.run {
            adapter = taskAdapter
            addItemDecoration(SimpleDividerItemDecoration(Color.DKGRAY, 0.5, context))
            helper.attachToRecyclerView(this)
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_task_list, menu)
        this.menu = menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.due_data_option -> {
            menu.findItem(R.id.creation_data_option).setIcon(0)
            taskListViewModel.getTasks { it.date }
            true
        }
        R.id.creation_data_option -> {
            menu.findItem(R.id.due_data_option).setIcon(0)
            taskListViewModel.getTasks { it.creationDate }
            true
        }
        R.id.setting_option -> {
//            findNavController().navigate(
//                    TaskListFragmentDirections.actionTaskListFragmentToSettingsFragment())
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun observeState() {
        lifecycle.coroutineScope.launchWhenStarted {
            taskListViewModel.sortByState.collect {

            }
        }
    }

    override fun onStart() {
        super.onStart()

        binding.addTask.setOnClickListener {
            val taskTitle = binding.newTaskEdit.text.toString()
            if (taskTitle.isBlank()) {
                changeFragment.replaceFragment(null)
            } else {
                quickTask(taskTitle)
            }
        }

        binding.newTaskEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val taskTitle = binding.newTaskEdit.text.toString()
                if (taskTitle.isNotBlank()) {
                    quickTask(taskTitle)
                } else {
                    changeFragment.replaceFragment("")
                }
            }
            false
        }
    }

    private fun quickTask(taskTitle: String) {
        binding.newTaskEdit.setText("")
        taskListViewModel.addTask(task = Task(titile = taskTitle))
    }

    private fun observeTaskList() {
        lifecycle.coroutineScope.launchWhenStarted {
            taskListViewModel.tasks.collect { list ->
                taskAdapter.submitList(list)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

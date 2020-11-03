package com.bignerdranch.android.todolist

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TaskListFragment:Fragment() {

    private val taskListViewModel: TaskListViewModel by lazy {
        ViewModelProvider(this).get(TaskListViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)

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
}
package com.bignerdranch.android.todolist

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import java.util.*

private const val TAG = "AddTask"

class AddTask:Fragment() {

    val args: AddTaskArgs by navArgs()
    private lateinit var task:Task
    private val taskViewModel:AddTaskViewModel by lazy {
        ViewModelProvider(this).get(AddTaskViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = null
        taskViewModel.loadTask(args.taskId as UUID)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_add_task, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskViewModel.
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_add_task, menu)

        menu.findItem(R.id.Done).apply {
            setOnMenuItemClickListener {
                Log.d("Test", "done")
                findNavController().popBackStack()
                true
            }
        }
        menu.findItem(R.id.discard).apply {
            setOnMenuItemClickListener {
                Log.d("Test", "discard")
                true
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return super.onContextItemSelected(item)
    }

    override fun onStop() {
        super.onStop()

    }

}
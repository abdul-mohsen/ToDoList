package com.bignerdranch.android.todolist

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

private const val TAG = "AddTask"
private const val DATE_KEY = "Date"


class AddTask:Fragment() {

    private lateinit var task:Task
    private lateinit var doneButton: ImageButton
    private lateinit var discardButton: ImageButton
    private lateinit var titleEdit: EditText
    private lateinit var tagEdit: EditText
    private lateinit var subtaskText: TextView
    private lateinit var subtaskEdit: EditText
    private lateinit var timeButton: Button
    private lateinit var locationButton: Button
    private lateinit var dueDateText: TextView
    private lateinit var descriptionEdit: EditText
    private lateinit var creationDateText: TextView
    private val taskViewModel:AddTaskViewModel by lazy {
        ViewModelProvider(this).get(AddTaskViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val args: AddTaskArgs by navArgs()
        super.onCreate(savedInstanceState)
        task = Task(id = args.taskId)
        (activity as AppCompatActivity).supportActionBar?.hide()
        taskViewModel.loadTask(args.taskId)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.fragment_add_task, container, false)

        doneButton = view.findViewById(R.id.done_button)
        discardButton = view.findViewById(R.id.discard_button)
        titleEdit = view.findViewById(R.id.title_edit)
        tagEdit = view.findViewById(R.id.tag_edit)
        subtaskText = view.findViewById(R.id.subtask_text)
        subtaskEdit = view.findViewById(R.id.suntask_edit)
        timeButton = view.findViewById(R.id.time_button)
        locationButton = view.findViewById(R.id.location_button)
        dueDateText = view.findViewById(R.id.due_date_text)
        descriptionEdit = view.findViewById(R.id.description_edit)
        creationDateText = view.findViewById(R.id.creation_date_text)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        taskViewModel.taskLiveData.observe(
            viewLifecycleOwner,
            { task -> task?.let {
                this.task = task
                updateUI()
            }}
        )

        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<Date>(DATE_KEY).observe(
                viewLifecycleOwner,
                {
                    task.date = it
                    updateUI()
                }
            )
        }
    }

    override fun onStart() {
        super.onStart()

        doneButton.setOnClickListener {
            task.titile = titleEdit.text.toString()
            task.description = descriptionEdit.text.toString()
            taskViewModel.updateTask(task)

            findNavController().navigateUp()
        }

        discardButton.setOnClickListener{
            context?.let {
                MaterialAlertDialogBuilder(it)
                    .setTitle("Discard Changes!!")
                    .setMessage("Do you want to discard changes")
                    .setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.cancel()
                        findNavController().navigateUp()
                    }
                    .setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.cancel()
                    }
                    .show()
            }
        }

        dueDateText.setOnClickListener {
            findNavController().navigate(
                AddTaskDirections.actionAddTaskToDatePickerFragment(taskDate = task.date, key = DATE_KEY)
            )

        }
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    fun updateUI(){
        creationDateText.text = task.creationDate.toString()
        titleEdit.setText(task.titile)
        dueDateText.text = task.date?.toString()?:"Date"
        descriptionEdit.setText(task.description)
    }
}
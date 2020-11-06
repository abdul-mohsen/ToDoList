package com.bignerdranch.android.todolist.addTask

import android.content.DialogInterface
import android.os.Bundle
import android.text.Spanned
import android.text.format.DateFormat
import android.text.style.ImageSpan
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bignerdranch.android.todolist.R
import com.bignerdranch.android.todolist.classes.Task
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

private const val DATE_KEY = "Date"
private const val TIME_KEY = "Time"
private const val DATE_FORMAT = "EEEE dd/MM/yy H:m"

class AddTask:Fragment() {

    private lateinit var task: Task
    private lateinit var doneButton: ImageButton
    private lateinit var discardButton: ImageButton
    private lateinit var titleEdit: EditText
    private lateinit var tagEdit: MultiAutoCompleteTextView
    private lateinit var subtaskText: TextView
    private lateinit var subtaskEdit: EditText
    private lateinit var timeButton: Button
    private lateinit var locationButton: Button
    private lateinit var dueDateText: TextView
    private lateinit var descriptionEdit: EditText
    private lateinit var creationDateText: TextView
    private lateinit var tagAdapter: ArrayAdapter<String>
    private val taskViewModel: AddTaskViewModel by lazy {
        ViewModelProvider(this).get(AddTaskViewModel::class.java)
    }
    private val tagList = listOf("Groceries", "Home", "Work","Groceries123").toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        val args: AddTaskArgs by navArgs()
        super.onCreate(savedInstanceState)
        task = Task(id = args.taskId)
        (activity as AppCompatActivity).supportActionBar?.hide()
        taskViewModel.loadTask(args.taskId)
        tagAdapter = ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, tagList)
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
            getLiveData<Date>(TIME_KEY).observe(
                viewLifecycleOwner,
                {date ->
                    task.date?.let {
                        it.hours = date.hours
                        it.minutes = date.minutes
                    }
                    updateUI()
                }
            )

            getLiveData<Date>(DATE_KEY).observe(
                viewLifecycleOwner,
                {date ->
                    if (task.date != null){
                        val cal = Calendar.getInstance()
                        val cal2 = Calendar.getInstance()
                        cal.time = date
                        cal2.time = task.date!!
                        cal.set(Calendar.HOUR,cal2.get(Calendar.HOUR))
                        cal.set(Calendar.MINUTE,cal2.get(Calendar.MINUTE))
                        task.date = cal.time
                    } else task.date = date
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
        timeButton.setOnClickListener {
            findNavController().navigate(
                AddTaskDirections.actionAddTaskToTimePickerFragment(
                    date = task.date,
                    key = TIME_KEY
                )
            )
        }
        dueDateText.setOnClickListener {
            findNavController().navigate(
                AddTaskDirections.actionAddTaskToDatePickerFragment(
                    taskDate = task.date,
                    key = DATE_KEY
                )
            )
        }

        tagEdit.setAdapter(tagAdapter)
        tagEdit.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

        tagEdit.setOnItemClickListener { _, _, _, _ ->
            val chip = ChipDrawable.createFromResource(requireContext(), R.xml.standalone_chip)
            val text = tagEdit.text
            val tempTags = text.toString().split(",").map { it.trim() }
            val tempTag = tempTags.last { !it.isBlank() }
            val startIndex = text.toString().lastIndexOf(tempTag)
            chip.text = tempTag
            chip.setBounds(0, 0, chip.intrinsicWidth, chip.intrinsicHeight)
            val span = ImageSpan(chip)
            text.setSpan(span, startIndex, startIndex + tempTag.length + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//            tagAdapter.clear()
//            tagAdapter.addAll(tagList)
            Log.d("Plz", "${text.toString()}  $startIndex |$tempTag|")
        }

//        tagEdit.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE){
//                val text = tagEdit.text.toString()
//                if (!text.isBlank() && text !in tagList) {
//                    tagList.add(text)
//                    tagAdapter.add(text)
//                    tagAdapter.notifyDataSetChanged()
//                }
//            }
//            Log.d("Plz","hmmmm")
//            false
//        }
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    private fun updateUI(){
        creationDateText.text = DateFormat.format(DATE_FORMAT, task.creationDate)
        titleEdit.setText(task.titile)
        task.date?.let{
            dueDateText.text = DateFormat.format(DATE_FORMAT, it)
        }
        descriptionEdit.setText(task.description)
    }
}
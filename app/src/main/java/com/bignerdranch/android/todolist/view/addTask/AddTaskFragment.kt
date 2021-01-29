package com.bignerdranch.android.todolist.view.addTask

import android.os.Bundle
import android.text.Spanned
import android.text.format.DateFormat
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.MultiAutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.bignerdranch.android.todolist.R
import com.bignerdranch.android.todolist.databinding.FragmentAddTaskBinding
import com.bignerdranch.android.todolist.model.ItemState
import com.bignerdranch.android.todolist.model.Task
import com.bignerdranch.android.todolist.view.MainActivity
import com.bignerdranch.android.todolist.view.dialog.CustomDialog
import com.bignerdranch.android.todolist.view.interfaces.INavigation
import com.google.android.material.chip.ChipDrawable
import kotlinx.coroutines.flow.collect
import timber.log.Timber
import java.util.*

class AddTaskFragment: Fragment() {

    private var _binding: FragmentAddTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var navigation: INavigation

    private lateinit var tagAdapter: ArrayAdapter<String>
    private lateinit var itemState: ItemState
    private val addTaskViewModel: AddTaskViewModel by lazy {
        ViewModelProvider(this).get(AddTaskViewModel::class.java)
    }
    private val tagList = listOf("Groceries", "Home", "Work","Groceries123").toMutableList()

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("hmmmm")
        super.onCreate(savedInstanceState)
        navigation = requireActivity() as INavigation
        val id: String? = arguments?.getString(MainActivity.TASK_ID)
        addTaskViewModel.getTaskData(id)
        observeTask()
        (activity as AppCompatActivity).supportActionBar?.hide()
        tagAdapter = ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, tagList)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        if (_binding == null)
            _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        binding.run {
            doneButton.setOnClickListener {
                val task = Task(
                    id = addTaskViewModel.task.value?.id ?: UUID.randomUUID(),
                    titile = binding.titleEdit.text.toString(),
                    description = binding.descriptionEdit.text.toString(),
                    date = addTaskViewModel.task.value?.date
                )
                addTaskViewModel.addTask(task)
                navigation.popUpStack()
            }
            discardButton.setOnClickListener {
                navigation.popUpStack()
            }
            timeButton.setOnClickListener {
//                navigation.showTimeFragment(DatePickerFragment(addTaskViewModel.task.value?.date?.time) { time ->
//                    addTaskViewModel.updateDate(time)
//                })
//                DatePickerFragment(addTaskViewModel.task.value?.date?.time) { time ->
//                    addTaskViewModel.updateDate(time)
//                }.show(childFragmentManager, null)
                CustomDialog.newInstance(addTaskViewModel.task.value?.date?.time) { time ->
                    addTaskViewModel.updateDate(time)
                }.show(childFragmentManager, CustomDialog.TAG)
            }
        }

        return binding.root
    }

    private fun datePlusTime(date: Date, time: Date): Date {
        val cal = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal.time = date
        cal2.time = time
        cal.set(Calendar.HOUR, cal2.get(Calendar.HOUR))
        cal.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE))
        return cal.time
    }

    override fun onStart() {
        super.onStart()

        binding.tagEdit.setAdapter(tagAdapter)
        binding.tagEdit.setTokenizer(MultiAutoCompleteTextView.CommaTokenizer())

        binding.tagEdit.setOnItemClickListener { _, _, _, _ ->
            val chip = ChipDrawable.createFromResource(requireContext(), R.xml.standalone_chip)
            val text = binding.tagEdit.text
            val tempTags = text.toString().split(",").map { it.trim() }
            val tempTag = tempTags.last { it.isNotBlank() }
            val startIndex = text.toString().lastIndexOf(tempTag)
            chip.text = tempTag
            chip.setBounds(0, 0, chip.intrinsicWidth, chip.intrinsicHeight)
            val span = ImageSpan(chip)
            text.setSpan(span, startIndex, startIndex + tempTag.length + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//            tagAdapter.clear()
//            tagAdapter.addAll(tagList)
            Timber.d("$text  $startIndex |$tempTag|")
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

    override fun onResume() {
        Timber.d("hmm")
        Timber.d("${addTaskViewModel.task.value?.date}")
        super.onResume()
    }

    private fun updateUI(task: Task?) {
        if (task == null) {
            binding.titleEdit.setText("")
            binding.descriptionEdit.setText("")
            binding.dueDateText.text = ""
        } else {
            binding.titleEdit.setText(task.titile)
            binding.descriptionEdit.setText(task.description)
            binding.dueDateText.text =
                if (task.date != null) DateFormat.format(DATE_FORMAT, task.date)
                else ""

            binding.creationDateText.text = DateFormat.format(DATE_FORMAT, task.creationDate)
        }
    }

    private fun observeTask() {
        lifecycle.coroutineScope.launchWhenStarted {
            addTaskViewModel.task.collect { task ->
                Timber.d("Update")
                updateUI(task)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Timber.d("onPause")
    }

    override fun onStop() {
        super.onStop()
        Timber.d("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy")
        _binding = null
    }

    companion object {
        private const val DATE_KEY = "Date"
        private const val TIME_KEY = "Time"
        private const val DATE_FORMAT = "EEEE dd/MM/yy HH:mm"
    }
}

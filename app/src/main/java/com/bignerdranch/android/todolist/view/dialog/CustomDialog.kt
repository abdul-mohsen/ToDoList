package com.bignerdranch.android.todolist.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.commit
import com.bignerdranch.android.todolist.R
import com.bignerdranch.android.todolist.databinding.FragmentCustomDialogBinding
import java.util.*

class CustomDialog(private val updateDate: (Long) -> Unit): DialogFragment() {
    private var _binding: FragmentCustomDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) _binding = FragmentCustomDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val oldTime = this.arguments?.getLong(KEY_TIME) ?: 0
        val datePickerFragment = DatePickerFragment(oldTime, ::dismiss) { time ->
            val timePickerFragment = TimePickerFragment(
                time,
                { childFragmentManager.popBackStack() }
            ) { selectedTime ->
                updateDate(selectedTime)
                dismiss()
            }
            childFragmentManager.commit {
                replace(R.id.fragment_dialog_container, timePickerFragment)
                addToBackStack(null)
            }
        }
        childFragmentManager.commit {
            replace(R.id.fragment_dialog_container, datePickerFragment)
            addToBackStack(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "MyDialog"

        private const val KEY_TIME = "KEY_TIME"

        fun newInstance(time: Long?, updateDate: (Long) -> Unit): CustomDialog =
            CustomDialog(updateDate).also { it.arguments = Bundle().apply { putLong(KEY_TIME, time?:Date().time) } }
    }
}
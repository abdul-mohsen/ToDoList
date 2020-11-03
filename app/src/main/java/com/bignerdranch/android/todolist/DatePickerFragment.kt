package com.bignerdranch.android.todolist

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import java.util.*

private const val DATE_KEY = "Date"

class DatePickerFragment: DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->

            val resultDate = GregorianCalendar(year, month, dayOfMonth).time
            findNavController().apply {
                previousBackStackEntry?.savedStateHandle?.set(DATE_KEY, resultDate)
                popBackStack()
            }


        }
        val args: DatePickerFragmentArgs by navArgs()
        val date:Date = args.taskDate?:Date()
        val calendar = Calendar.getInstance()
        calendar.time = date
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
            dateListener,
            initialYear,
            initialMonth,
            initialDay
        )
    }
}
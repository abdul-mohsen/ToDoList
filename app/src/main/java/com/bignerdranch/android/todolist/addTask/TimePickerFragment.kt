package com.bignerdranch.android.todolist.addTask

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import java.util.*

class TimePickerFragment: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val args: TimePickerFragmentArgs by navArgs()
        val calendar = Calendar.getInstance()
        val date = args.date?:Date()
        calendar.time = date
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val dateListener = TimePickerDialog.OnTimeSetListener{
                _:TimePicker, hourOfDay: Int, minute:Int ->
            val resultDate :Date = GregorianCalendar(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                hourOfDay,minute).time
            findNavController().apply {
                previousBackStackEntry?.savedStateHandle?.set(args.key, resultDate)
                popBackStack()
            }
        }
        return TimePickerDialog(requireContext(), dateListener, hour, minute, is24HourFormat(requireContext()))
    }
}
package com.bignerdranch.android.todolist.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.bignerdranch.android.todolist.databinding.FragmentCustomDialogBinding
import com.bignerdranch.android.todolist.databinding.FragmentDatePickerBinding
import java.util.*

class DatePickerFragment(
    private val time: Long,
    private val negative: () -> Unit,
    private val positive: (Long) -> Unit
): Fragment() {
    private var _binding: FragmentDatePickerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) _binding = FragmentDatePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calendar = Calendar.getInstance()
        calendar.time = Date(time)
        binding.run {
            binding.datePicker.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ) { _: DatePicker, Year: Int, Month: Int, DayOfMonth: Int ->
                calendar.run {
                    set(Calendar.YEAR, Year)
                    set(Calendar.MONTH, Month)
                    set(Calendar.DAY_OF_MONTH, DayOfMonth)
                }
            }
            btnPositive.setOnClickListener {
                positive(calendar.time.time)
            }
            btnNegative.setOnClickListener {
                negative()
            }
        }
    }

    companion object {
        val TAG = "DatePickerFragment"
    }
}

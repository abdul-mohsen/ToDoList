package com.bignerdranch.android.todolist.view.dialog

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bignerdranch.android.todolist.databinding.FragmentTimePickerBinding
import java.util.*

class TimePickerFragment(
    private val time: Long,
    private val negative: () -> Unit,
    private val positive: (Long) -> Unit
): Fragment() {
    private var _binding: FragmentTimePickerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) _binding = FragmentTimePickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calendar = Calendar.getInstance()
        calendar.time = Date(time)

        binding.run {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                timePicker.run {
                    hour = calendar.get(Calendar.HOUR_OF_DAY)
                    minute = calendar.get(Calendar.MINUTE)
                }
            else
                timePicker.run {
                    currentHour = calendar.get(Calendar.HOUR)
                    currentMinute = calendar.get(Calendar.MINUTE)
                }
            btnPositive.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    calendar.set(Calendar.HOUR, timePicker.hour)
                    calendar.set(Calendar.MINUTE, timePicker.minute)
                } else {
                    calendar.set(Calendar.HOUR, timePicker.currentHour)
                    calendar.set(Calendar.MINUTE, timePicker.currentMinute)
                }
                positive(calendar.time.time)
            }
            btnNegative.setOnClickListener {
                negative()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        val TAG = "TimePickerFragment"
    }
}
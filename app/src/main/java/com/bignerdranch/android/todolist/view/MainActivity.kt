package com.bignerdranch.android.todolist.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.bignerdranch.android.todolist.R
import com.bignerdranch.android.todolist.view.addTask.AddTaskFragment
import com.bignerdranch.android.todolist.databinding.ActivityMainBinding
import com.bignerdranch.android.todolist.view.interfaces.INavigation
import com.bignerdranch.android.todolist.view.taskList.TaskListFragment

class MainActivity : AppCompatActivity(), INavigation {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add<TaskListFragment>(R.id.nav_host_fragment)
            }
        }
    }

    override fun replaceFragment(id: String?) {
        supportFragmentManager.commit {
            replace<AddTaskFragment>(
                R.id.nav_host_fragment,
                args = Bundle().apply {putString(TASK_ID, id) })
            addToBackStack(null)
        }
    }

    override fun popUpStack() {
        supportFragmentManager.popBackStack()
    }

    override fun showTimeFragment(fragment: DialogFragment) {
        fragment.show(supportFragmentManager, null)
    }

    companion object {
        const val TASK_ID = "TASK_ID"
    }
}

package com.bignerdranch.android.todolist.view.interfaces

import androidx.fragment.app.DialogFragment

interface INavigation {
    fun replaceFragment(id: String?)
    fun popUpStack()
    fun showTimeFragment(fragment: DialogFragment)
}
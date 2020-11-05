package com.bignerdranch.android.todolist.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bignerdranch.android.todolist.classes.Tag
import com.bignerdranch.android.todolist.classes.Task
import com.bignerdranch.android.todolist.classes.TaskTagCrossRef
import java.util.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    fun getTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE id=(:id)")
    fun getTask(id: UUID): LiveData<Task?>

    @Update
    fun updateTask(task: Task)

    @Insert
    fun insertTask(task: Task)

    @Delete
    fun deleteTask(task: Task)

}
package com.bignerdranch.android.todolist.database

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.bignerdranch.android.todolist.classes.TaskWithTags

interface TaskWithTagsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(join: TaskWithTags)
}
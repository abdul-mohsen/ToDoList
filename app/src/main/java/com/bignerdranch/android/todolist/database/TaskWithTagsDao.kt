package com.bignerdranch.android.todolist.database

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.bignerdranch.android.todolist.model.TaskWithTags

interface TaskWithTagsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(join: TaskWithTags)
}
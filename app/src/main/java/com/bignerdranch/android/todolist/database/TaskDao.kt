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

//    @Transaction
//    @Query("SELECT * FROM Task WHERE id=()")
//    fun getTaskWithTags(): List<TaskWithTags>

    @Query(
        "SELECT tagId FROM TaskWithTags" +
                "INNER JOIN Tag ON Tag.id = TaskWithTags.tagId " +
                "WHERE id=(:id)"
    )
    fun getTaskTags(id: UUID): LiveData<List<Tag>>

    data class TaskWithTags(
        @Embedded val task: Task,
        @Relation(
            parentColumn = "taskId",
            entityColumn = "tagId",
            associateBy = Junction(TaskTagCrossRef::class)
        )
        val tags: List<Tag>
    )


    @Update
    fun updateTask(task: Task)

    @Insert
    fun insertTask(task: Task)

    @Delete
    fun deleteTask(task: Task)

}
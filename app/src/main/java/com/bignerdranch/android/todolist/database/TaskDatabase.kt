package com.bignerdranch.android.todolist.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bignerdranch.android.todolist.Tag
import com.bignerdranch.android.todolist.Task
import com.bignerdranch.android.todolist.TaskTagCrossRef

@Database(entities = [Task::class, Tag::class, TaskTagCrossRef::class], version=2)
@TypeConverters(TaskTypeConverters::class)
abstract class TaskDatabase: RoomDatabase(){
    abstract fun taskDao(): TaskDao

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE 'Tag' ('id' TEXT, 'tag' TEXT, PRIMARY KEY('id'))"
            )
            database.execSQL(
                "CREATE TABLE 'TaskWithTags' ('taskid' TEXT, 'tag' TEXT, PRIMARY KEY('id'))"
            )
        }
    }
}


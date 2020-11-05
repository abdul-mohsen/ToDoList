package com.bignerdranch.android.todolist.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bignerdranch.android.todolist.classes.Tag
import com.bignerdranch.android.todolist.classes.Task
import com.bignerdranch.android.todolist.classes.TaskTagCrossRef


@Database(entities = [Task::class, Tag::class, TaskTagCrossRef::class], version=2)
@TypeConverters(TaskTypeConverters::class)
abstract class TaskDatabase: RoomDatabase(){
    abstract fun taskDao(): TaskDao

}


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.apply {
            execSQL(
                "ALTER TABLE Task ADD COLUMN 'priority' INTEGER"
            )
            execSQL(
                "CREATE TABLE 'Tag' ('id' TEXT NOT NULL, 'tag' TEXT NOT NULL, PRIMARY KEY('id'))"
            )
            execSQL("""
                    CREATE TABLE TaskTagCrossRef (taskId TEXT NOT NULL, tagId TEXT NOT NULL, PRIMARY KEY(taskId , tagId))
                """
            )

        }

    }
}


package com.bignerdranch.android.todolist.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bignerdranch.android.todolist.model.Tag
import com.bignerdranch.android.todolist.model.Task
import com.bignerdranch.android.todolist.model.TaskTagCrossRef


@Database(entities = [Task::class, Tag::class, TaskTagCrossRef::class], version=2)
@TypeConverters(TaskTypeConverters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.apply {
                    execSQL("ALTER TABLE Task ADD COLUMN 'priority' INTEGER")
                    execSQL("CREATE TABLE 'Tag' ('id' TEXT NOT NULL, 'tag' TEXT NOT NULL, PRIMARY KEY('id'))")
                    execSQL("CREATE TABLE TaskTagCrossRef (taskId TEXT NOT NULL, tagId TEXT NOT NULL, PRIMARY KEY(taskId , tagId))")
                }
            }
        }
        private const val DATABASE_NAME = "task-database"
        @Volatile
        private var INSTANCE: AppDatabase? = null
        operator fun invoke(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .addMigrations(MIGRATION_1_2)
                .build()
    }
}


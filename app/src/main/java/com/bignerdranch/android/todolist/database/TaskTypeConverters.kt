package com.bignerdranch.android.todolist.database

import androidx.room.TypeConverter
import com.bignerdranch.android.todolist.Status
import java.util.*

class TaskTypeConverters {
    @TypeConverter
    fun fromDate(date: Date?): Long? = date?.time?.toLong()

    @TypeConverter
    fun fromTimestamp(value:Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun toUUID(uuid: String?): UUID? = UUID.fromString(uuid)

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? = uuid?.toString()

    @TypeConverter
    fun toStatus(index: Int?): Status? = Status.values()[index?:0]

    @TypeConverter
    fun fromStatus(state: Status?): Int? = state?.ordinal
}
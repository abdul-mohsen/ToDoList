package com.bignerdranch.android.todolist.classes

import android.os.Parcelable
import androidx.room.*
import com.bignerdranch.android.todolist.Priority
import com.bignerdranch.android.todolist.Status
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity
@Parcelize
data class Task(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var titile: String = "",
    var description: String = "",
    var date: Date? = null,
    val creationDate: Date = Date(),
    var status: Status = Status.InProgress,
    var priority: Priority? = Priority.Medium
) : Parcelable
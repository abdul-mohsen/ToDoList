package com.bignerdranch.android.todolist.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
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
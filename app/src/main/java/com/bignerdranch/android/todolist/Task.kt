package com.bignerdranch.android.todolist

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
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
    var status: Status = Status.InProgress
) : Parcelable

enum class Status{
    Achieved, Upcoming, Overdue, InProgress, SomeDay
}

enum class SortOptionStatus{
    IDLE, DES, AES

}
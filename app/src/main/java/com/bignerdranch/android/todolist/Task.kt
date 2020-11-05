package com.bignerdranch.android.todolist

import android.os.Parcelable
import androidx.room.*
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
    var tags: List<Tag> = emptyList()
) : Parcelable


@Entity(primaryKeys = ["taskId","tagId"])
@Parcelize
data class TaskTagCrossRef(
    val taskId: UUID,
    val tagId: UUID
): Parcelable

data class TaskWithTags(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "taskId",
        entityColumn = "tagId",
        associateBy = Junction(TaskTagCrossRef::class)
    )
    val tags: List<Tag>
)

enum class Status{
    Achieved, Upcoming, Overdue, InProgress, SomeDay
}

enum class SortOptionStatus{
    IDLE, DES, AES
}
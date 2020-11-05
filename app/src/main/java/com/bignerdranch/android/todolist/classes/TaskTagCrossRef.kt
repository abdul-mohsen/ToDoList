package com.bignerdranch.android.todolist.classes

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(primaryKeys = ["taskId","tagId"])
@Parcelize
data class TaskTagCrossRef(
    val taskId: UUID,
    val tagId: UUID
): Parcelable
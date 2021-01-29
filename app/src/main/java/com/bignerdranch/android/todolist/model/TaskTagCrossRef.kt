package com.bignerdranch.android.todolist.model

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(primaryKeys = ["taskId","tagId"])
@Parcelize
data class TaskTagCrossRef(
    val taskId: UUID,
    val tagId: UUID
): Parcelable
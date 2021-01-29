package com.bignerdranch.android.todolist.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity
@Parcelize
data class Tag(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var tag: String = "",
) : Parcelable

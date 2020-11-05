package com.bignerdranch.android.todolist.classes

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity
@Parcelize
data class Tag(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var tag: String = "",
) : Parcelable

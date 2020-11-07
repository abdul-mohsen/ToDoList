package com.bignerdranch.android.todolist

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

enum class Status{
    Achieved, Upcoming, Overdue, InProgress, SomeDay
}

enum class SortOptionStatus{
    IDLE, DES, AES
}

inline fun <reified T: Enum<T>> T.next(): T {
    val values = enumValues<T>()
    val nextOrdinal = (ordinal + 1) % values.size
    return values[nextOrdinal]
}

enum class Priority{
    VeryHigh, High, Medium, Low, VeryLow
}

enum class ItemState{
    Add, Update, Delete
}

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}
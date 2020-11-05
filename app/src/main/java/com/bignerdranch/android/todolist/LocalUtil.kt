package com.bignerdranch.android.todolist

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
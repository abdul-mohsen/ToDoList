package com.bignerdranch.android.todolist.taskList

import android.graphics.Canvas
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class DragManagerAdapter(
    private val adapter: TaskAdapter,
    dragDirs: Int,
    swipeDirs: Int)
    : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs){

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = true
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.absoluteAdapterPosition
        when (direction) {
            ItemTouchHelper.RIGHT -> adapter.update(position, true)
            ItemTouchHelper.LEFT -> adapter.update(position, false)
            else -> Log.d("Testing", "hmmm")
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
    }

    override fun getAnimationDuration(
        recyclerView: RecyclerView,
        animationType: Int,
        animateDx: Float,
        animateDy: Float
    ): Long = 0

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float = 10F
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 0.3F

}
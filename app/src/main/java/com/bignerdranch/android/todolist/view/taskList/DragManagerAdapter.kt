package com.bignerdranch.android.todolist.view.taskList

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

class DragManagerAdapter(
    dragDirs: Int,
    swipeDirs: Int,
    private val update: (Int, Boolean) -> Unit
) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs){

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = true
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.absoluteAdapterPosition
        when (direction) {
            ItemTouchHelper.RIGHT -> update(position, true)
            ItemTouchHelper.LEFT -> update(position, false)
            else -> Timber.d( "hmmm")
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
    ) {  }

    override fun getAnimationDuration(
        recyclerView: RecyclerView,
        animationType: Int,
        animateDx: Float,
        animateDy: Float
    ): Long = 0

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float = 10F
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float = 0.3F

}
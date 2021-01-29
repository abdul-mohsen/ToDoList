package com.bignerdranch.android.todolist.view.taskList

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import kotlin.math.roundToInt

class SimpleDividerItemDecoration(
    private val color: Int,
    private val height: Double,
    private val context: Context?
) : RecyclerView.ItemDecoration() {
    private val paint = Paint()

    init {
        paint.isAntiAlias = true
    }

    override fun getItemOffsets(rect: Rect, v: View, parent: RecyclerView, s: RecyclerView.State) {
        parent.adapter?.let { adapter ->
            val childAdapterPosition = parent.getChildAdapterPosition(v)
                .let { if (it == RecyclerView.NO_POSITION) return else it }
            rect.right = // Add space/"padding" on right side
                if (childAdapterPosition == adapter.itemCount - 1) 0    // No "padding"
                else height.roundToInt()                       // Drawable width "padding"
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child
                .layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top.toFloat() + pxFromDp(height.toFloat())
            Timber.d("W = ${pxFromDp(height.toFloat())}")
            paint.color = color
            c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom, paint)
        }
    }

    private fun pxFromDp(dp: Float): Float {
        return if (context == null) dp
        else dp * context.resources.displayMetrics.density
    }
}
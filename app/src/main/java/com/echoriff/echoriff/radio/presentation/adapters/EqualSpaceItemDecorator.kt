package com.echoriff.echoriff.radio.presentation.adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class EqualSpaceItemDecoration(private val spaceWidth: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount

        if (position == 0) {
            // No margin for the first item
            outRect.left = spaceWidth
        } else {
            // Add space to the left of every item except the first one
            outRect.left = spaceWidth / 2
        }

        if (position == itemCount - 1) {
            // No margin for the last item
            outRect.right = spaceWidth
        } else {
            // Add space to the right of every item except the last one
            outRect.right = spaceWidth / 2
        }
    }
}
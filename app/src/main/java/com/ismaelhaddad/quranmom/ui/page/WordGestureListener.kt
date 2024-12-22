package com.ismaelhaddad.quranmom.ui.page

import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.abs

class WordGestureListener(
    val onSwipeLeft: () -> Unit,
    val onClick: () -> Unit
) : GestureDetector.SimpleOnGestureListener() {

    companion object {
        private const val SWIPE_THRESHOLD = 200
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        if (e1 != null) {
            val diffX = e2.x - e1.x
            if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX < 0) { // Left swipe
                    onSwipeLeft()
                    return true
                }
            }
        }
        return false
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        onClick()
        return true
    }
}
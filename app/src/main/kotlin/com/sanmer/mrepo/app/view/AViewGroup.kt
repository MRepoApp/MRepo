package com.sanmer.mrepo.app.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/**
 * From [LibChecker](https://github.com/LibChecker/LibChecker.git)
 */
abstract class AViewGroup(context: Context, attributeSet: AttributeSet? = null) :
    ViewGroup(context, attributeSet) {

    private fun View.defaultWidthMeasureSpec(parentView: ViewGroup): Int {
        return when (layoutParams.width) {
            ViewGroup.LayoutParams.MATCH_PARENT -> parentView.measuredWidth.toExactlyMeasureSpec()
            ViewGroup.LayoutParams.WRAP_CONTENT -> ViewGroup.LayoutParams.WRAP_CONTENT.toAtMostMeasureSpec()
            0 -> throw IllegalAccessException("Need special treatment for $this")
            else -> layoutParams.width.toExactlyMeasureSpec()
        }
    }

    private fun View.defaultHeightMeasureSpec(parentView: ViewGroup): Int {
        return when (layoutParams.height) {
            ViewGroup.LayoutParams.MATCH_PARENT -> parentView.measuredHeight.toExactlyMeasureSpec()
            ViewGroup.LayoutParams.WRAP_CONTENT -> ViewGroup.LayoutParams.WRAP_CONTENT.toAtMostMeasureSpec()
            0 -> throw IllegalAccessException("Need special treatment for $this")
            else -> layoutParams.height.toExactlyMeasureSpec()
        }
    }

    private fun Int.toExactlyMeasureSpec(): Int {
        return MeasureSpec.makeMeasureSpec(this, MeasureSpec.EXACTLY)
    }

    private fun Int.toAtMostMeasureSpec(): Int {
        return MeasureSpec.makeMeasureSpec(this, MeasureSpec.AT_MOST)
    }

    protected fun View.autoMeasure() {
        measure(
            this.defaultWidthMeasureSpec(parentView = this@AViewGroup),
            this.defaultHeightMeasureSpec(parentView = this@AViewGroup)
        )
    }

    protected fun View.toHorizontalCenter(parentView: ViewGroup): Int {
        return (parentView.measuredWidth - measuredWidth) / 2
    }

    protected fun View.layout(x: Int, y: Int, fromRight: Boolean = false) {
        if (!fromRight) {
            layout(x, y, x + measuredWidth, y + measuredHeight)
        } else {
            layout(this@AViewGroup.measuredWidth - x - measuredWidth, y)
        }
    }

    protected val Int.dp: Int get() = (this * resources.displayMetrics.density + 0.5f).toInt()

    protected class LayoutParams(width: Int, height: Int) : MarginLayoutParams(width, height)
}

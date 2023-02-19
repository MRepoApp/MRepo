package com.sanmer.mrepo.app.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.sanmer.mrepo.R

/**
 * From [LibChecker](https://github.com/LibChecker/LibChecker.git)
 */
class ToastView(context: Context) : AViewGroup(context) {

    val message = AppCompatTextView(context).apply {
        layoutParams =
            LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        maxWidth = 300.dp
        gravity = Gravity.CENTER
        setTextAppearance(android.R.style.TextAppearance_Material_Body2)
        setTextColor(Color.BLACK)
        val padding = 12.dp
        setPadding(padding, padding, padding, padding)
        setBackgroundResource(R.drawable.toast_background)
        addView(this)
    }

    private val icon = AppCompatImageView(context).apply {
        layoutParams = LayoutParams(24.dp, 24.dp)
        setImageResource(R.mipmap.ic_launcher)
        addView(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        message.autoMeasure()
        icon.autoMeasure()
        setMeasuredDimension(
            message.measuredWidth,
            message.measuredHeight + icon.measuredHeight / 2
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        icon.let { it.layout(it.toHorizontalCenter(this), 0) }
        message.layout(0, icon.measuredHeight / 2)
    }
}
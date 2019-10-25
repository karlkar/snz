package com.karol.sezonnazdrowie.view.controls

import android.content.Context
import android.util.AttributeSet

import androidx.appcompat.widget.AppCompatImageView

class SquareImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(
    context,
    attrs,
    defStyle
) {

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            widthMeasureSpec
        ) // This is the key that will make the height equivalent to its width
    }
}
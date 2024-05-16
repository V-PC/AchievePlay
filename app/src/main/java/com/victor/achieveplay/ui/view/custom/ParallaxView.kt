package com.victor.achieveplay.ui.view.custom

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.widget.ImageView

class ParallaxView(context: Context, attrs: AttributeSet) : androidx.appcompat.widget.AppCompatImageView(context, attrs) {

    fun setOffset(offsetX: Float, offsetY: Float) {
        val matrix = Matrix(getImageMatrix())
        matrix.setTranslate(offsetX, offsetY)
        setImageMatrix(matrix)
    }
}

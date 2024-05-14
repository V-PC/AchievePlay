package com.victor.achieveplay

import android.content.Context
import android.graphics.Matrix
import android.util.AttributeSet
import android.widget.ImageView

class ParallaxView(context: Context, attrs: AttributeSet) : ImageView(context, attrs) {

    fun setOffset(offsetX: Float, offsetY: Float) {
        val matrix = Matrix(getImageMatrix())
        matrix.setTranslate(offsetX, offsetY)
        setImageMatrix(matrix)
    }
}

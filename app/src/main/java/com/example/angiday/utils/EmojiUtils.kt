package com.example.angiday.utils

import android.content.Context
import android.graphics.*

object EmojiUtils {

    fun textToBitmap(text: String, size: Float, context: Context): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = size
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.LEFT
        paint.typeface = Typeface.DEFAULT

        val baseline = -paint.ascent()
        val width = paint.measureText(text)
        val height = baseline + paint.descent()

        val bitmap = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawText(text, 0f, baseline, paint)

        return bitmap
    }
}

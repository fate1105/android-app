package com.example.angiday.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.angiday.R
import java.io.File
import java.io.FileOutputStream

object ImageUtils {
    fun getDrawableId(context: Context, name: String?): Int {
        if (name.isNullOrEmpty()) return R.drawable.ic_launcher_foreground
        val resId = context.resources.getIdentifier(name, "drawable", context.packageName)
        return if (resId != 0) resId else R.drawable.ic_launcher_foreground
    }

    fun loadCachedImage(file: File?): Bitmap? {
        return if (file != null && file.exists()) {
            BitmapFactory.decodeFile(file.path)
        } else null
    }
}

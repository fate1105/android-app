package com.example.angiday.util

import android.content.Context
import com.example.angiday.R

object ImageUtils {
    fun getDrawableId(context: Context, name: String?): Int {
        if (name.isNullOrEmpty()) return R.drawable.ic_launcher_foreground
        val resId = context.resources.getIdentifier(name, "drawable", context.packageName)
        return if (resId != 0) resId else R.drawable.ic_launcher_foreground
    }
}

package com.example.angiday.db.providers

import android.content.Context
import java.io.File

object DatabaseCopier {

    fun copyPrepopulatedDB(context: Context) {
        val dbName = "angiday.db"
        val dbPath = context.getDatabasePath(dbName)

        // Nếu DB đã tồn tại → KHÔNG COPY NỮA
        if (dbPath.exists()) return

        dbPath.parentFile?.mkdirs()

        context.assets.open("databases/$dbName").use { input ->
            dbPath.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }
}

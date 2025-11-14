package com.example.angiday.db.providers

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.room.Room
import com.example.angiday.db.AppDatabase
import com.example.angiday.model.entity.FoodEntity

class FoodContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.example.angiday.provider"
        private const val TABLE_NAME = "foods"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$TABLE_NAME")

        private const val FOODS = 1
        private const val FOOD_ID = 2

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, TABLE_NAME, FOODS)
            addURI(AUTHORITY, "$TABLE_NAME/#", FOOD_ID)
        }
    }

    private lateinit var db: AppDatabase

    override fun onCreate(): Boolean {
        val ctx = requireNotNull(context)
        db = AppDatabase.get(ctx)   // ✅ dùng singleton
        Log.d("FoodProvider", "Provider initialized with shared DB")
        return true
    }


    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val dao = db.foodDao()
        val cursor = when (uriMatcher.match(uri)) {
            FOODS -> dao.getAllCursor()
            FOOD_ID -> {
                val id = ContentUris.parseId(uri)
                dao.getByIdCursor(id)
            }
            else -> null
        }
        cursor?.setNotificationUri(context?.contentResolver, uri)
        Log.d("FoodProvider", "📥 Query called: $uri")
        return cursor
    }

    override fun getType(uri: Uri): String? = when (uriMatcher.match(uri)) {
        FOODS -> "vnd.android.cursor.dir/$AUTHORITY.$TABLE_NAME"
        FOOD_ID -> "vnd.android.cursor.item/$AUTHORITY.$TABLE_NAME"
        else -> null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        Log.d("FoodProvider", "Insert called: $uri")
        throw UnsupportedOperationException("Insert not supported")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        Log.d("FoodProvider", "Update called: $uri")
        throw UnsupportedOperationException("Update not supported")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        Log.d("FoodProvider", "Delete called: $uri")
        throw UnsupportedOperationException("Delete not supported")
    }
}
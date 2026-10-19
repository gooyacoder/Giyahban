package com.ahm.giyahban

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class TaskDatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_TASK)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $DB_TABLE")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "tasks_db"
        private const val DB_TABLE = "tasks"

        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_MESSAGE = "message"
        private const val KEY_DATE = "date"

        private const val CREATE_TABLE_TASK = "CREATE TABLE $DB_TABLE (" +
                "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$KEY_TITLE TEXT NOT NULL, " +
                "$KEY_MESSAGE TEXT, " +
                "$KEY_DATE INTEGER NOT NULL);"
    }

    @Throws(SQLiteException::class)
    fun addTask(task: Task): Long {
        return addTask(task.title, task.message, task.date)
    }

    @Throws(SQLiteException::class)
    fun addTask(title: String, message: String, date: Long): Long {
        val db = this.writableDatabase
        val cv = ContentValues().apply {
            put(KEY_TITLE, title)
            put(KEY_MESSAGE, message)
            put(KEY_DATE, date)
        }
        val result = db.insert(DB_TABLE, null, cv)
        db.close()
        return result
    }

    @Throws(SQLiteException::class)
    fun getAllTasks(): MutableList<Task> {
        val taskList = mutableListOf<Task>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $DB_TABLE ORDER BY $KEY_DATE ASC"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndexOrThrow(KEY_ID)
            val titleIndex = cursor.getColumnIndexOrThrow(KEY_TITLE)
            val messageIndex = cursor.getColumnIndexOrThrow(KEY_MESSAGE)
            val dateIndex = cursor.getColumnIndexOrThrow(KEY_DATE)

            do {
                val id = cursor.getLong(idIndex)
                val title = cursor.getString(titleIndex) ?: ""
                val message = cursor.getString(messageIndex) ?: ""
                val date = cursor.getLong(dateIndex)

                taskList.add(Task(id = id, title = title, message = message, date = date))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return taskList
    }

    @Throws(SQLiteException::class)
    fun getTaskById(id: Long): Task? {
        val db = this.readableDatabase
        val cursor = db.query(
            DB_TABLE,
            arrayOf(KEY_ID, KEY_TITLE, KEY_MESSAGE, KEY_DATE),
            "$KEY_ID = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        )

        var task: Task? = null
        if (cursor.moveToFirst()) {
            val title = cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)) ?: ""
            val message = cursor.getString(cursor.getColumnIndexOrThrow(KEY_MESSAGE)) ?: ""
            val date = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_DATE))
            task = Task(id = id, title = title, message = message, date = date)
        }

        cursor.close()
        db.close()
        return task
    }

    @Throws(SQLiteException::class)
    fun updateTask(task: Task): Int {
        val db = this.writableDatabase
        val cv = ContentValues().apply {
            put(KEY_TITLE, task.title)
            put(KEY_MESSAGE, task.message)
            put(KEY_DATE, task.date)
        }
        val rowsAffected = db.update(DB_TABLE, cv, "$KEY_ID = ?", arrayOf(task.id.toString()))
        db.close()
        return rowsAffected
    }

    @Throws(SQLiteException::class)
    fun deleteTask(id: Long): Int {
        val db = this.writableDatabase
        val rowsAffected = db.delete(DB_TABLE, "$KEY_ID = ?", arrayOf(id.toString()))
        db.close()
        return rowsAffected
    }

    @Throws(SQLiteException::class)
    fun deleteTask(task: Task): Int {
        return deleteTask(task.id)
    }
}

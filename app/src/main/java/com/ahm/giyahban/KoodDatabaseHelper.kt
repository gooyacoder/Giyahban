package com.ahm.giyahban

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper



class KoodDatabaseHelper (context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    lateinit var koods: MutableList<String>

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(CREATE_TABLE_KOOD)
    }

    override fun onUpgrade(
        p0: SQLiteDatabase?,
        p1: Int,
        p2: Int
    ) {
        p0?.execSQL("DROP TABLE IF EXISTS $DB_TABLE")
        onCreate(p0);
    }

    companion object {
        // Database Version
        private const val DATABASE_VERSION = 1

        // Database Name
        private const val DATABASE_NAME = "kood_db"
        private const val DB_TABLE = "table_kood"
        private const val KEY_NAME = "kood_name"

        private const val CREATE_TABLE_KOOD = "CREATE TABLE " + DB_TABLE + "(" +
                KEY_NAME + " TEXT);"

    }

    @Throws(SQLiteException::class)
    fun addEntry(name: String): Long {
        val database = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_NAME, name)

        val result = database.insert(
            DB_TABLE,
            null,
            cv
        ) // returns -1 if insert fails, show message that the plant name exists.
        database.close()
        return result
    }

    @JvmName("getPlants1")
    fun getKoods(): MutableList<String> {
        koods = ArrayList()
        val db = this.writableDatabase
        val query = "SELECT * FROM " + DB_TABLE
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val kood = cursor.getString(0)
            koods.add(kood)
        }
        cursor.close()
        db.close()
        return koods
    }

}
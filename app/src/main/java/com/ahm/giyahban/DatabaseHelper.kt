package com.ahm.giyahban

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    var plants = mutableListOf<Plant>()


    override fun onCreate(db: SQLiteDatabase) {

        // creating table
        db.execSQL(CREATE_TABLE_PLANT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE)

        // create new table
        onCreate(db)
    }

    companion object {
        // Database Version
        private const val DATABASE_VERSION = 1

        // Database Name
        private const val DATABASE_NAME = "plants_db"

        // Table Names
        private const val DB_TABLE = "plant_table"

        // column names
        private const val KEY_NAME = "plant_name"
        private const val KEY_IMAGE = "plant_image"
        private const val KEY_PLANT_WATER_DATE = "plant_water_date"
        private const val KEY_PLANT_WATER_PERIOD = "plant_water_period"
        private const val KEY_PLANT_FERTILIZERS = "plant_fertilizers"
        private const val KEY_PLANT_FERTILIZER_DATES = "plant_fertilizer_dates"
        private const val KEY_PLANT_FERTILIZER_PERIODS = "plant_fertilizer_periods"

        // Table create statement
        private const val CREATE_TABLE_PLANT = "CREATE TABLE " + DB_TABLE + "(" +
                KEY_NAME + " TEXT NOT NULL UNIQUE," +
                KEY_IMAGE + " BLOB," +
                KEY_PLANT_WATER_DATE + " TEXT," +
                KEY_PLANT_WATER_PERIOD + " TEXT," +
                KEY_PLANT_FERTILIZERS + " TEXT," +
                KEY_PLANT_FERTILIZER_DATES + " TEXT," +
                KEY_PLANT_FERTILIZER_PERIODS + " TEXT);"

    }

    @Throws(SQLiteException::class)
    fun addEntry(name: String,
                 image: ByteArray,
                 water_date: Long?,
                 water_period: Int?,
                 fertilizers: ArrayList<String>?,
                 fertilizer_dates: ArrayList<String>?,
                 fertilizer_periods: ArrayList<String>?) {
        val database = this.writableDatabase
        val cv = ContentValues()
        val water_date_string = water_date.toString()
        val water_period_string = water_period.toString()
        val fertilizers_string = convertArrayToString(fertilizers)
        val fertilizer_dates_string = convertArrayToString(fertilizer_dates)
        val fertilizer_periods_string = convertArrayToString(fertilizer_periods)
        cv.put(KEY_NAME, name)
        cv.put(KEY_IMAGE, image)
        cv.put(KEY_PLANT_WATER_DATE, water_date_string)
        cv.put(KEY_PLANT_WATER_PERIOD, water_period_string)
        cv.put(KEY_PLANT_FERTILIZERS, fertilizers_string)
        cv.put(KEY_PLANT_FERTILIZER_DATES, fertilizer_dates_string)
        cv.put(KEY_PLANT_FERTILIZER_PERIODS, fertilizer_periods_string)
        database.insert(DB_TABLE, null, cv) // returns -1 if insert fails, show message that the plant name exists.
        database.close()
    }

    @JvmName("getPlants1")
    fun getPlants(): MutableList<Plant> {
        val db = this.writableDatabase
        val query = "SELECT * FROM " + DB_TABLE
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val name = cursor.getString(0)
            val imagebyte = cursor.getBlob(1)
            val water_date = cursor.getString(2)
            val water_period = cursor.getString(3)
            val fertilizers = cursor.getString(4)
            val fertilizer_dates = cursor.getString(5)
            val fertilizer_periods = cursor.getString(6)
            val plant = Plant(name,
                imagebyte,
                water_date.toLong(),
                water_period.toInt(),
                convertArrayToArrayList(convertStringToArray(fertilizers)),
                convertArrayToArrayList(convertStringToArray(fertilizer_dates)),
                convertArrayToArrayList(convertStringToArray(fertilizer_periods)))
            plants.add(plant)
        }
        cursor.close()
        db.close()
        return plants
    }

    fun removePlant(name: String?){
        val db = this.writableDatabase
        db.execSQL("delete from plant_table where" +
                " plant_name = \"" + name + "\";");
        db.close()
    }

    fun updatePlant(name: String?,
                    image: ByteArray?,
                    water_date: Long?,
                    water_period: Int?,
                    fertilizers: ArrayList<String>?,
                    fertilizer_dates: ArrayList<String>?,
                    fertilizer_periods: ArrayList<String>?){

    }

    fun convertArrayToString(array: ArrayList<String>?): String? {
        val strSeparator = "__,__"
        var str = ""
        if(array != null){
            for (i in array.indices) {
                str = str + array[i]
                // Do not append comma at the end of last element
                if (i < array.size - 1) {
                    str = str + strSeparator
                }
            }
        }
        return str
    }

    fun convertStringToArray(str: String): Array<String>? {
        val strSeparator = "__,__"
        return str.split(strSeparator).toTypedArray()
    }
    fun convertArrayToArrayList(array_data: Array<String>?): ArrayList<String>? {
        val arrayList: ArrayList<String> = ArrayList<String>()
        for (i in array_data!!){
            arrayList.add(i)
        }
        return arrayList
    }
}
package com.ahm.giyahban

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.io.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString


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


        // Table create statement
        private const val CREATE_TABLE_PLANT = "CREATE TABLE " + DB_TABLE + "(" +
                KEY_NAME + " TEXT NOT NULL UNIQUE," +
                KEY_IMAGE + " BLOB," +
                KEY_PLANT_WATER_DATE + " TEXT," +
                KEY_PLANT_WATER_PERIOD + " TEXT," +
                KEY_PLANT_FERTILIZERS + " TEXT);"

    }

    @Throws(SQLiteException::class)
    fun updatePlants(plants: MutableList<Plant>) {
        val database = this.writableDatabase
        for (plant in plants) {
            val cv = ContentValues()
            cv.put(KEY_NAME, plant.plant_name)
            cv.put(KEY_IMAGE, plant.image)
            cv.put(KEY_PLANT_WATER_DATE, plant.water_date)
            cv.put(KEY_PLANT_WATER_PERIOD, plant.water_period)
            if(plant.fertilizers == null){
                cv.put(KEY_PLANT_FERTILIZERS, Json.encodeToString(java.util.ArrayList<Fertilizer>(0)))
            }else{
                cv.put(KEY_PLANT_FERTILIZERS, Json.encodeToString(plant.fertilizers))
            }

            database.insert(DB_TABLE, null, cv)
        }
        database.close()
    }

    @Throws(SQLiteException::class)
    fun addEntry(name: String, image: ByteArray): Long {
        val database = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_NAME, name)
        cv.put(KEY_IMAGE, image)
        val result = database.insert(
            DB_TABLE,
            null,
            cv
        ) // returns -1 if insert fails, show message that the plant name exists.
        database.close()
        return result
    }


    @Throws(SQLiteException::class)
    fun addFertilizer(plantName: String?, fertilizers: String?) {
        val updateFertilizerQuery =
            "update $DB_TABLE set $KEY_PLANT_FERTILIZERS = '$fertilizers' where $KEY_NAME = '$plantName';"
        val db = this.writableDatabase
        db.execSQL(updateFertilizerQuery)
        db.close()
    }

    fun removeFertilizer(plantName: String?, fertilizers: String?) {
        val db = this.writableDatabase
        val updateFertilizerQuery =
            "update $DB_TABLE set $KEY_PLANT_FERTILIZERS = '$fertilizers' where $KEY_NAME = '$plantName';"
        db.execSQL(updateFertilizerQuery)
        db.close()
    }

    @Throws(SQLiteException::class)
    fun addWatering(plantName: String?, date: String?, days: String?) {
        val updateWateringQuery =
            "update $DB_TABLE set $KEY_PLANT_WATER_DATE = '$date', $KEY_PLANT_WATER_PERIOD = '$days' where $KEY_NAME = '$plantName';"
        val db = this.writableDatabase
        db.execSQL(updateWateringQuery)
        db.close()
    }

    @Throws(SQLiteException::class)
    fun getWateringDays(plantName: String?): String? {
        var days = ""
        val plants = this.getPlants()
        for (plant in plants) {
            if (plant.plant_name == plantName) {
                days = plant.water_period.toString()
            }
        }
        return days
    }

    fun removeWatering(plantName: String?) {
        val db = this.writableDatabase
        val updateWateringQuery =
            "update $DB_TABLE set $KEY_PLANT_WATER_DATE = NULL, $KEY_PLANT_WATER_PERIOD = NULL where $KEY_NAME = '$plantName';"
        db.execSQL(updateWateringQuery)
        db.close()
    }

    fun getPlant(name: String?): Plant {
        val db = this.writableDatabase
        val query = "SELECT * FROM $DB_TABLE where $KEY_NAME = '$name';"
        val cursor = db.rawQuery(query, null)
        cursor.moveToNext()
        val name = cursor.getString(0)
        val imagebyte = cursor.getBlob(1)
        val water_date = cursor.getString(2)
        val water_period = cursor.getString(3)
        val fert_string = cursor.getString(4)
        var fertilizers: ArrayList<Fertilizer>? = null
        if (fert_string != null) {
            fertilizers = Json.decodeFromString(fert_string)
        }
        val plant = Plant(
            name,
            imagebyte,
            if (water_date != null) water_date.toLong() else null,
            if (water_period != null) water_period.toInt() else null,
            if (fertilizers != null) fertilizers else null
        )
        db.close()
        return plant
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
            val fert_string = cursor.getString(4)
            var fertilizers: ArrayList<Fertilizer>? = null
            if (fert_string != null) {
                fertilizers = Json.decodeFromString(fert_string)
            }
            val plant = Plant(
                name,
                imagebyte,
                if (water_date != null) water_date.toLong() else null,
                if (water_period != null) water_period.toInt() else null,
                if (fertilizers != null) fertilizers else null
            )
            plants.add(plant)
        }
        cursor.close()
        db.close()
        return plants
    }

    fun removePlant(name: String?) {
        val db = this.writableDatabase
        db.execSQL(
            "delete from plant_table where" +
                    " plant_name = \"" + name + "\";"
        );
        db.close()
    }

    fun updatePlantImage(name: String, image: ByteArray) : Int{
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_NAME, name)
        cv.put(KEY_IMAGE, image)
        val args = Array<String>(1) { name }
        val result = db.update(DB_TABLE, cv, "plant_name=?", args)
        db.close()
        return result
    }

    fun updatePlantWaterDate(name: String, water_date: Long) {

        var update_query = ""
        val water_date_string = water_date.toString()
        update_query =
            "update $DB_TABLE set $KEY_PLANT_WATER_DATE = '$water_date_string' where $KEY_NAME = '$name';"
        val db = this.writableDatabase
        db.execSQL(update_query)
        db.close()

    }

    fun updatePlantWaterPeriod(name: String, water_period: Int) {
        var update_query = ""
        val water_period_string = water_period.toString()
        update_query =
            "update $DB_TABLE set $KEY_PLANT_WATER_PERIOD = '$water_period_string' where $KEY_NAME = '$name';"
        val db = this.writableDatabase
        db.execSQL(update_query)
        db.close()
    }

    fun updatePlantFertilizers(name: String, fertilizers: ArrayList<Fertilizer>) {
        var update_query = ""
        val fertilizers_string = Json.encodeToString(fertilizers)
        update_query =
            "update $DB_TABLE set $KEY_PLANT_FERTILIZERS = '$fertilizers_string' where $KEY_NAME = '$name';"
        val db = this.writableDatabase
        db.execSQL(update_query)
        db.close()
    }


    @JvmName("getFertilizers1")
    fun getFertilizers(name: String?): MutableMap<ArrayList<String>?, ArrayList<String>?>? {
        val db = this.writableDatabase
        val query = "SELECT * FROM $DB_TABLE where $KEY_NAME='$name';"
        val cursor = db.rawQuery(query, null)
        var fert_string = ""
        var fertilizers: ArrayList<Fertilizer>? = ArrayList()
        var fertilizers_names: ArrayList<String>? = ArrayList()
        var fertilizers_periods: ArrayList<String>? = ArrayList()
        // read bytearray

        if (cursor.moveToNext()) {
            if (cursor.getString(4) != null) {
                fert_string = cursor.getString(4)
                fertilizers = Json.decodeFromString(fert_string)
            }
        }
        if (fertilizers != null) {
            for (i in fertilizers) {
                fertilizers_names?.add(i.name)
                fertilizers_periods?.add(i.period.toString())
            }
        }
        cursor.close()
        db.close()
        val result: MutableMap<ArrayList<String>?, ArrayList<String>?>? = HashMap()
        result?.set(fertilizers_names, fertilizers_periods)
        return result
    }

    @JvmName("getFertilizers1")
    fun getFertilizersArrayList(name: String?): ArrayList<Fertilizer>? {
        val db = this.writableDatabase
        val query = "SELECT * FROM $DB_TABLE where $KEY_NAME='$name';"
        val cursor = db.rawQuery(query, null)
        var fertilizers: ArrayList<Fertilizer>? = ArrayList()
        var fert_string = ""
        if (cursor.moveToNext()) {
            if (cursor.getString(4) != null) {
                fert_string = cursor.getString(4)
                fertilizers = Json.decodeFromString(fert_string)
            }
        }
        cursor.close()
        db.close()
        return fertilizers
    }


    fun getFertilizerDates(name: String?): ArrayList<String>? {
        val db = this.writableDatabase
        val query = "SELECT * FROM $DB_TABLE where $KEY_NAME='$name';"
        val cursor = db.rawQuery(query, null)
        var fertilizerDatesArrayList: ArrayList<String>? = ArrayList()
        var fertilizers: ArrayList<Fertilizer>? = ArrayList()
        if (cursor.moveToNext()) {
            if (cursor.getString(4) != null) {
                var fert_string = cursor.getString(4)
                fertilizers = Json.decodeFromString(fert_string)
            }
        }
        if (fertilizers != null) {
            for (fert in fertilizers) {
                fertilizerDatesArrayList?.add(fert.date.toString())
            }
        }

        cursor.close()
        db.close()
        return fertilizerDatesArrayList
    }

    fun updatePlant(plant: Plant) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_NAME, plant.plant_name)
        cv.put(KEY_IMAGE, plant.image)
        cv.put(KEY_PLANT_WATER_DATE, plant.water_date)
        cv.put(KEY_PLANT_WATER_PERIOD, plant.water_period)
        cv.put(KEY_PLANT_FERTILIZERS, Json.encodeToString(plant.fertilizers))
        db.insert(DB_TABLE, null, cv)
        db.close()
    }


}
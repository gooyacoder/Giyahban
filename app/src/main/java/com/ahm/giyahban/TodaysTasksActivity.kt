package com.ahm.giyahban

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.util.*

class TodaysTasksActivity : AppCompatActivity() {

    var TasksList : ListView? = null
    var today: Long = 0
    val water: MutableList<Boolean> = mutableListOf()
    val fertilizerList : MutableList<MutableSet<String>> = mutableListOf()
    val plant_names_list : MutableList<String> = mutableListOf()
    val plant_images_list : MutableList<Bitmap> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todays_tasks)
        prepareUI()
        getTodaysTasks()
    }

    private fun getTodaysTasks() {
        val db = DatabaseHelper(this)
        val plants = db.getPlants()

        if (plants.size > 0) {
            for (plant in plants) {
                if(hasTask(plant)){
                    val plant_image = DbBitmapUtility.getImage(plant.image)
                    plant_images_list.add(plant_image)
                    val plant_name = plant.plant_name
                    plant_names_list.add(plant_name)
                    var watering_time_passed: Int = 0
                    if(plant.water_date != null){
                        watering_time_passed = calculateDays(today - plant.water_date!!.toLong())
                    }
                    val fertilizer_time_passed : ArrayList<Int> = ArrayList()
                    val fert_dates = db.getFertilizerDates(plant.plant_name)
                    for (frt in fert_dates!!){
                        if(frt.isNotEmpty())
                            fertilizer_time_passed.add(calculateDays(today - frt.toLong()))
                    }
                    val list: MutableSet<String> = mutableSetOf()
                    val fert_names = db.getFertilizersNames(plant.plant_name)
                    val fert_periods = db.getFertilizerPeriodsArrayList(plant.plant_name)

                    for(fertNameIndex in fert_names!!.indices){
                        if(fert_periods!![fertNameIndex] == ""){
                            continue
                        }
                        if(fert_periods!![fertNameIndex].toInt() == fertilizer_time_passed[fertNameIndex]){
                            list.add(fert_names[fertNameIndex])
                        }

                    }
                    fertilizerList.add(list)
                    if(plant.water_date != null){
                        if(plant.water_period!! == watering_time_passed){
                            water.add(true)
                        }
                    }else {
                        water.add(false)
                    }
                }
            }
        }
        val customPlantList = CustomPlantList2(this, plant_names_list, plant_images_list, water, fertilizerList)
        TasksList?.adapter = customPlantList
        var plantsToWater = ""
        water.forEachIndexed{i, w ->
            if(w){
                plantsToWater += plant_names_list[i] + ", "
            }
        }
        //Toast.makeText(this, plantsToWater, Toast.LENGTH_LONG).show()
        val waterTextView = findViewById<TextView>(R.id.wateringTextView)
        waterTextView.setText(waterTextView.text.toString() + plantsToWater)
        db.close()
    }

    private fun hasTask(plant: Plant): Boolean {
        val db = DatabaseHelper(this)
        var result = false
        if(plant.water_date != null){
            val watering_time_passed = calculateDays(today - plant.water_date!!.toLong())
            if(watering_time_passed == plant.water_period!!.toInt()){
                result = true
            }
        }

        val fertilizer_time_passed : ArrayList<Int> = ArrayList()

        val fert_dates = db.getFertilizerDates(plant.plant_name)
        val fert_periods = db.getFertilizerPeriodsArrayList(plant.plant_name)
        if(fert_dates != null){
            for (frt in fert_dates){
                if(frt.isNotEmpty()){
                    fertilizer_time_passed.add(calculateDays(today - frt.toLong()))
                }

            }
            var i = 0
            for(ft in fertilizer_time_passed){
                if(i < fert_periods!!.size && fert_periods!![i].isNotEmpty()){
                    if(ft == fert_periods!![i].toInt()){
                        result = true
                    }
                }
                i++
            }
        }

        db.close()
        return result
    }

    private fun calculateDays(l: Long): Int {
        return (l/(1000*60*60*24)).toInt()
    }

    private fun prepareUI() {
        TasksList = findViewById(R.id.tasks_list)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 5)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        today = calendar.getTime().getTime()
    }

    fun resetButtonClicked(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to reset Today's Tasks?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                resetTasks()
                water.clear()
                fertilizerList.clear()
                plant_names_list.clear()
                plant_images_list.clear()
                val waterTextView = findViewById<TextView>(R.id.wateringTextView)
                waterTextView.setText("")
                getTodaysTasks()
                Toast.makeText(this, "Tasks Reset Successfully.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()

    }

    private fun resetTasks() {
        val db = DatabaseHelper(this)
        val plants = db.getPlants()
        var i = 0
        for(plant in plant_names_list){
            if(water[i]){
                db.updatePlantWaterDate(plant, today)
            }
            if(fertilizerList[i].size > 0) {
                val currentPlant = plants[i]
                val updatedFertilizersDates = currentPlant.fertilizer_dates
                val fertilizers = currentPlant.fertilizers
                var j = 0
                for(fert in fertilizerList[i]){
                    if(fert == fertilizers!![j]){
                        updatedFertilizersDates!![j] = today.toString()
                    }
                    if(j < fertilizerList[i].size - 1){
                        j++
                    }
                }
                db.updatePlantFertilizerDates(plant, updatedFertilizersDates!!)
            }
            if(i < plant_names_list.size - 1){
                i++
            }
        }
        db.close()
    }

    /*fun makebyte(modeldata: Dataobject?): ByteArray? {
        try {
            val baos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(baos)
            oos.writeObject(modeldata)
            val employeeAsBytes: ByteArray = baos.toByteArray()
            val bais = ByteArrayInputStream(employeeAsBytes)
            return employeeAsBytes
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }


    fun read(data: ByteArray?): Dataobject? {
        try {
            val baip = ByteArrayInputStream(data)
            val ois = ObjectInputStream(baip)
            return ois.readObject() as Dataobject
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        return null
    }*/


}
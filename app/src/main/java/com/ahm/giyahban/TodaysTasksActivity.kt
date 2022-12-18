package com.ahm.giyahban

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.util.*
import kotlin.collections.HashMap

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
        db.close()
        if (plants.size > 0) {
            for (plant in plants) {
                if(hasTask(plant)){
                    val plant_image = DbBitmapUtility.getImage(plant.image)
                    plant_images_list.add(plant_image)
                    val plant_name = plant.plant_name
                    plant_names_list.add(plant_name)
                    val watering_time_passed = calculateDays(today - plant.water_date!!.toLong())
                    val fertilizer_time_passed : ArrayList<Int> = ArrayList()
                    for (frt in plant.fertilizer_dates!!){
                        fertilizer_time_passed.add(calculateDays(today - frt.toLong()))
                    }
                    val list: MutableSet<String> = mutableSetOf()
                    for( i in fertilizer_time_passed){
                        if(plant.fertilizer_periods!!.contains(i.toString())){
                            list.add(plant.fertilizers!!.elementAt(plant.fertilizer_periods!!.indexOf(i.toString())))
                        }
                    }
                    fertilizerList.add(list)
                    if(plant.water_period!! == watering_time_passed){
                        water.add(true)
                    }
                    else {
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

    }

    private fun hasTask(plant: Plant): Boolean {
        var result = false
        val watering_time_passed = calculateDays(today - plant.water_date!!.toLong())
        val fertilizer_time_passed : ArrayList<Int> = ArrayList()

        for (frt in plant.fertilizer_dates!!){
            fertilizer_time_passed.add(calculateDays(today - frt.toLong()))
        }
        var i = 0
        for(ft in fertilizer_time_passed){
            if(ft == plant.fertilizer_periods!!.elementAt(i).toInt()){
                result = true
            }
            if(i < plant.fertilizer_periods.size - 1){
                i++
            }
        }
        if(watering_time_passed == plant.water_period!!.toInt()){
            result = true
        }
        return result
    }

    private fun calculateDays(l: Long): Int {
        return (l/(1000*60*60*24)).toInt()
    }

    private fun prepareUI() {
        TasksList = findViewById(R.id.tasks_list)
        val calendar = Calendar.getInstance()
        today = calendar.getTime().getTime()
    }

    fun resetButtonClicked(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to reset Today's Tasks?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                resetTasks()
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
        Toast.makeText(this, "Tasks Reset Successfully.", Toast.LENGTH_LONG).show()
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
                val updatedFertilizers = currentPlant.fertilizer_dates
                val fertilizers = currentPlant.fertilizers
                var j = 0
                for(fert in fertilizerList[i]){
                    if(fert == fertilizers!![j]){
                        updatedFertilizers?.set(j, today.toString())
                    }
                    if(j < fertilizerList[i].size - 2){
                        j++
                    }
                }
                db.updatePlantFertilizerDates(plant, updatedFertilizers!!)
            }
            if(i < plant_names_list.size - 2){
                i++
            }
        }
        db.close()
    }

}
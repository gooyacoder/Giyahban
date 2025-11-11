package com.ahm.giyahban

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class TodaysWateringsActivity : AppCompatActivity() {


    var today: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todays_waterings)
        prepareUI()
        showTodayWaterings()
    }

    private fun showTodayWaterings() {
        val db = DatabaseHelper(this)
        val plants = db.getPlants()
        db.close()

        val parentLayout = findViewById<ViewGroup>(R.id.linearLayoutTodayWaterings)
        if (plants.size > 0) {
            for (plant in plants) {
                if(hasWatering(plant)){
                    val plant_image = DbBitmapUtility.getImage(plant.image)
                    val plant_name = plant.plant_name
                    val inflater = LayoutInflater.from(this)
                    val childView = inflater.inflate(R.layout.waterings_layout, parentLayout, false)
                    val imageView = childView.findViewById<ImageView>(R.id.watering_plantImage)

                    imageView.setImageBitmap(plant_image)
                    val textView = childView.findViewById<TextView>(R.id.watering_plantName)
                    textView.setText(plant_name)
                    parentLayout.addView(childView)
                }
            }
        }
    }


    private fun hasWatering(plant: Plant): Boolean {
        val db = DatabaseHelper(this)
        var result = false
        if(plant.water_date != null){
            val watering_time_passed = calculateDays(today - plant.water_date.toLong())
            if(watering_time_passed == 0){
                if((watering_time_passed + 1) % (plant.water_period!!.toInt() + 1) == 0){
                    result = true
                }
            }
            else {
                if((watering_time_passed) % (plant.water_period!!.toInt()) == 0) {
                    result = true
                }
            }

        }
        db.close()
        return result
    }

    private fun calculateDays(l: Long): Int {
        return (l/(1000*60*60*24)).toInt()
    }

    private fun prepareUI() {
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
                //resetTasks()
                //showTodayWaterings()
                Toast.makeText(this, "Tasks Reset Successfully.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun resetTasks() {
        val db = DatabaseHelper(this)
        val plants = db.getPlants()
        for(plant in plants){
            if(hasWatering(plant)){
                db.updatePlantWaterDate(plant.plant_name, today)
            }
        }
        db.close()
    }
}

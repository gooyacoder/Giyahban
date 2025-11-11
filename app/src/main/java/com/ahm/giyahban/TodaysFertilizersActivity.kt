package com.ahm.giyahban

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import java.util.Calendar

class TodaysFertilizersActivity : AppCompatActivity() {
    var today: Long = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todays_fertilizers)
        prepareUI()
        showTodayFertilizers()
    }

    private fun showTodayFertilizers() {
        val db = DatabaseHelper(this)
        val plants = db.getPlants()
        db.close()

        val parentLayout = findViewById<ViewGroup>(R.id.linearLayoutTodayFertilizers)
        if (plants.size > 0) {
            for (plant in plants) {
                if(hasFertilizer(plant)){
                    val plant_image = DbBitmapUtility.getImage(plant.image)
                    val plant_name = plant.plant_name
                    val inflater = LayoutInflater.from(this)
                    val childView = inflater.inflate(R.layout.fertilizers_layout, parentLayout, false)
                    val imageView = childView.findViewById<ImageView>(R.id.fertilizer_plantImage)
                    imageView.setImageBitmap(plant_image)
                    val textView = childView.findViewById<TextView>(R.id.fertilizer_plantName)
                    textView.setText(plant_name)
                    val textView2 = childView.findViewById<TextView>(R.id.fertilizer_plantFertilizers)
                    var fert_text = ""
                    for(fertilizer in plant.fertilizers!!){
                        val fertilizer_time_passed = calculateDays(today - fertilizer.date.toLong())
                        if(fertilizer_time_passed == 0){
                            if((fertilizer_time_passed + 1) % (fertilizer.period.toInt() + 1) == 0){
                                fert_text += fertilizer.name + ", "
                            }
                        }
                        else{
                            if(fertilizer_time_passed % fertilizer.period.toInt() == 0){
                                fert_text += fertilizer.name + ", "
                            }
                        }

                    }
                    textView2.setText(fert_text)
                    parentLayout.addView(childView)
                }
            }
        }
    }

    private fun hasFertilizer(plant: Plant): Boolean {
        val db = DatabaseHelper(this)
        var result = false
        if(plant.fertilizers != null){
            for(fertilizer in plant.fertilizers){
                val fertilizer_time_passed = calculateDays(today - fertilizer.date.toLong())
                if(fertilizer_time_passed == 0){
                    if((fertilizer_time_passed + 1) % (fertilizer.period.toInt() + 1) == 0){
                        result = true
                    }
                }
                else{
                    if(fertilizer_time_passed % fertilizer.period.toInt() == 0){
                        result = true
                    }
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
        today = calendar.time.time
    }

    private fun resetTasks() {
        val db = DatabaseHelper(this)
        val plants = db.getPlants()
        for(plant in plants){
            if(hasFertilizer(plant)){
                if(plant.fertilizers != null){
                    for(fertilizer in plant.fertilizers){
                        val fertilizer_time_passed = calculateDays(today - fertilizer.date.toLong())
                        if(fertilizer_time_passed == fertilizer.period.toInt()){
                            fertilizer.date = today
                        }
                    }
                    db.updatePlantFertilizers(plant.plant_name, plant.fertilizers)
                }
            }
        }
        db.close()
    }
    fun resetFertilizersButtonClicked(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to reset Today's Tasks?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                // resetTasks()
                Toast.makeText(this, "Tasks Reset Successfully.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, id ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}

package com.ahm.giyahban

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DeletePlantActivity : AppCompatActivity() {

    var plants_names: ArrayList<String>? = null
    var plant_position = 0
    var plants_images: ArrayList<Bitmap>? = null
    var plantImageView: ImageView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_plant)
        getPlantsFromDatabase()
        plantImageView = findViewById(R.id.plantImageView)
        if(plants_images!!.size > 0){
            plantImageView?.setImageBitmap(plants_images?.get(plant_position))
        }
    }
    private fun getPlantsFromDatabase() {
        val db = DatabaseHelper(this)
        val plants = db.getPlants()
        db.close()
        plants_names = ArrayList<String>()
        plants_images = ArrayList<Bitmap>()
        for (plant in plants) {
            plants_names!!.add(plant.plant_name)
            plants_images!!.add(DbBitmapUtility.getImage(plant.image))
        }
        val items = plants_names!!.toTypedArray()
        val adapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            this, R.layout.custom_spinner_view, items
        )

        var namesDropDownSpinner = findViewById<Spinner>(R.id.plants_spinner)
        namesDropDownSpinner.setAdapter(adapter)
        namesDropDownSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                plant_position = position
                plantImageView?.setImageBitmap(DbBitmapUtility.getImage(plants.get(plant_position).image))

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    fun deleteBtn_Clicked(view: View) {
        val builder = AlertDialog.Builder(this)
        val plant_name = plants_names?.get(plant_position)
        builder.setMessage("Are you sure you want to Delete $plant_name?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                val db = DatabaseHelper(this)
                db.removePlant(plant_name)
                db.close()
                plants_names?.clear()
                plants_images?.clear()
                getPlantsFromDatabase()
                plantImageView?.setImageBitmap(plants_images?.get(plant_position))
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}

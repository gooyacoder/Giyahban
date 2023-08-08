package com.ahm.giyahban

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginEnd

class AllPlantsActivity : AppCompatActivity() {
    //var listView: ListView? = null
    var index: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_plants)

        prepareUI()
    }

    private fun prepareUI() {
        val db = DatabaseHelper(this)
        val plants = db.getPlants()
        db.close()

        val parentLayout = findViewById<ViewGroup>(R.id.linearLayoutAllPlants)
        if (plants.size > 0) {
            for (plant in plants) {
                val plant_image = DbBitmapUtility.getImage(plant.image)
                val plant_name = plant.plant_name
                val inflater = LayoutInflater.from(this)
                val childView = inflater.inflate(R.layout.all_plants_layout, parentLayout, false)
                val imageView = childView.findViewById<ImageView>(R.id.plantImage)

                imageView.setImageBitmap(plant_image)
                val textView = childView.findViewById<TextView>(R.id.plantName)
                textView.setText(plant_name)
                parentLayout.addView(childView)
            }
        }
    }
}
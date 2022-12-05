package com.ahm.giyahban

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView

class AllPlantsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_plants)
        prepareUI()
    }

    private fun prepareUI() {
        val db = DatabaseHelper(this)
        val plants = db.getPlants()
        db.close()
        val plant_names_list : MutableList<String> = mutableListOf()
        val plant_images_list : MutableList<Bitmap> = mutableListOf()
        plant_names_list.clear()
        if (plants.size > 0) {
            for (plant in plants) {
                val plant_image = DbBitmapUtility.getImage(plant.image)
                plant_images_list.add(plant_image)
                val plant_name = plant.plant_name
                plant_names_list.add(plant_name)

            }
        }
        val customPlantList = CustomPlantList(this, plant_names_list, plant_images_list)
        val listView: ListView = findViewById(R.id.list)
        listView?.adapter = customPlantList
    }

    fun delete_button_cliced(view: View) {

    }
}
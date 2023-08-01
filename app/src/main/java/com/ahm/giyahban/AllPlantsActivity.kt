package com.ahm.giyahban

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog

class AllPlantsActivity : AppCompatActivity() {
    var listView: ListView? = null
    var index: Int = 0
    val plant_names_list : MutableList<String> = mutableListOf()
    val plant_images_list : MutableList<Bitmap> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_plants)
        listView = findViewById(R.id.list)
        listView?.onItemClickListener = object : AdapterView.OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>, view: View,
                                     position: Int, id: Long) {
                index = position
            }
        }
    //    prepareUI()
    }

//    private fun prepareUI() {
//        val db = DatabaseHelper(this)
//        val plants = db.getPlants()
//        db.close()
//
//        plant_names_list.clear()
//        if (plants.size > 0) {
//            for (plant in plants) {
//                val plant_image = DbBitmapUtility.getImage(plant.image)
//                plant_images_list.add(plant_image)
//                val plant_name = plant.plant_name
//                plant_names_list.add(plant_name)
//
//            }
//        }
//        val customPlantList = CustomPlantList(this, plant_names_list, plant_images_list)
//        listView?.adapter = customPlantList
//    }

    fun delete_button_clicked(view: View) {
        val builder = AlertDialog.Builder(this)
        val plantName = plant_names_list[index]
        builder.setMessage("Are you sure you want to Delete the \"$plantName\"?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                val db = DatabaseHelper(this)
                db.removePlant(plantName)
                db.close()
               // prepareUI()
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }
}
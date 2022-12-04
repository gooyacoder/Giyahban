package com.ahm.giyahban

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun add_plant_btn_clicked(view: View) {

        val intent = Intent(this, AddPlantActivity::class.java)
        startActivity(intent)

    }
    fun edit_plant_btn_clicked(view: View) {

        val intent = Intent(this, EditPlantActivity::class.java)
        startActivity(intent)

    }
    fun show_plants_tasks_btn_clicked(view: View) {

        val intent = Intent(this, TodaysTasksActivity::class.java)
        startActivity(intent)

    }
    fun show_plants_list_btn_clicked(view: View) {

        val intent = Intent(this, AllPlantsActivity::class.java)
        startActivity(intent)

    }
}
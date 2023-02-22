package com.ahm.giyahban



import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity


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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.giyahban_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.getItemId()) {
            R.id.save -> {
                // Save()
                true
            }
            R.id.update -> {
                // Update()
                true
            }
            R.id.exit -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun Save() {

    }

    fun update() {

    }
}
package com.ahm.giyahban



import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.*


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
                Save()
                true
            }
            R.id.update -> {
                update()
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
        writeFileOnExternalStorage()
    }

    fun update() {
        val text = readFileFromExternalStorage()
        val plants: MutableList<Plant> = Json.decodeFromString(text!!)
        val db = DatabaseHelper(this)
        try {
            db.updatePlants(plants)
        }catch (e: Exception){
            e.printStackTrace()
        }
        db.close()
        Toast.makeText(applicationContext, "Plants Data Updated.", Toast.LENGTH_LONG).show()
    }

    fun writeFileOnExternalStorage() {
        val db = DatabaseHelper(this)
        val plants = db.getPlants()
        db.close()
        val plants_string = Json.encodeToString(plants)
        val myExternalFile = File(getExternalFilesDir("giyahban"), "Data")
        if(!myExternalFile.exists())
            myExternalFile.mkdirs()
        try {
            val fileToWrite = File(myExternalFile, "plants.txt")
            val fileOutPutStream = FileOutputStream(fileToWrite)
            fileOutPutStream.write(plants_string.toByteArray())
            fileOutPutStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Toast.makeText(applicationContext,"Plants Data Saved.",Toast.LENGTH_SHORT).show()
    }

    fun readFileFromExternalStorage(): String? {
        var myExternalFile = File(getExternalFilesDir("giyahban/Data"), "plants.txt")
        var text: String? = null
        var fileInputStream = FileInputStream(myExternalFile)
        var inputStreamReader: InputStreamReader = InputStreamReader(fileInputStream)
        val bufferedReader: BufferedReader = BufferedReader(inputStreamReader)
        val stringBuilder: StringBuilder = StringBuilder()

        while ({ text = bufferedReader.readLine(); text }() != null) {
            stringBuilder.append(text)
        }
        fileInputStream.close()
        return stringBuilder.toString()
    }


}
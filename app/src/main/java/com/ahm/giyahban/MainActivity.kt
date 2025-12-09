package com.ahm.giyahban



import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // Android 11+
            if (!Environment.isExternalStorageManager()) {
                // Request MANAGE_EXTERNAL_STORAGE permission
                val intent: Intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", getPackageName(), null)
                intent.setData(uri)
                startActivityForResult(intent, 1)
            }
        }
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

        val intent = Intent(this, TaskChooserActivity::class.java)
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
        var mainHandler = Handler(Looper.getMainLooper());

        var myRunnable = Runnable() {
            run() {
                writeFileOnExternalStorage()
            }
        };
        mainHandler.post(myRunnable);
    }

    fun update() {
        var mainHandler = Handler(Looper.getMainLooper());

        var myRunnable = Runnable() {
            run() {
                updateFromFile()
            }
        };
        mainHandler.post(myRunnable);
    }

    private fun updateFromFile() {
        val text = readFileFromExternalStorage()
        val plants: MutableList<Plant> = Json.decodeFromString(text!!)
        val db = DatabaseHelper(this)
        try {
            db.updatePlants(plants)
        } catch (e: Exception) {
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
        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

        try {
            val fileToWrite = File(dir, "plants.txt")
            val fileOutPutStream = FileOutputStream(fileToWrite)
            fileOutPutStream.write(plants_string.toByteArray())
            fileOutPutStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Toast.makeText(applicationContext, "Plants Data Saved.", Toast.LENGTH_SHORT).show()
    }

    fun readFileFromExternalStorage(): String? {

        val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        var myExternalFile = File(dir, "plants.txt")
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


    fun delete_plant_btn_clicked(view: View) {
        val intent = Intent(this, DeletePlantActivity::class.java)
        startActivity(intent)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // Permission granted
                } else {
                    // Permission denied
                }
            }
        }
    }
}
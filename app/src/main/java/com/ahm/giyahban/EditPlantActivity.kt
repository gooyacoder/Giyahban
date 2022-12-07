package com.ahm.giyahban


import android.os.Bundle
import android.view.View
import android.widget.AbsListView.CHOICE_MODE_SINGLE
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class EditPlantActivity : AppCompatActivity() {

    var list_position = 1
    var plant_position = 1
    var namesDropDownSpinner : Spinner? = null
    var plants_names: ArrayList<String>? = null
    var fertilizer_list: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_plant)
        prepareUI()
        getPlantsFromDatabase()
        prepareFertilizerDropDownSpinner()
        prepareFertilizerList()
        updateFertilizerList()
        updateCurrentWatering()
    }

    private fun prepareUI() {
        namesDropDownSpinner = findViewById(R.id.PlantsDropDownSpinner)
        namesDropDownSpinner?.setOnItemClickListener{ parent, view, position, id ->
            plant_position = position
        }
        fertilizer_list = findViewById(R.id.fertilizer_list)
    }

    private fun prepareFertilizerList() {

        fertilizer_list?.choiceMode = CHOICE_MODE_SINGLE
        fertilizer_list?.setOnItemClickListener { parent, view, position, id ->
            list_position = position
        }
    }

    private fun prepareFertilizerDropDownSpinner() {
        val items = arrayOf( "20-20-20", "12-12-36", "10-52-10", "3-11-38",
            "هیومیک اسید", "قارچ کش", "اسید آمینه", "جلبک دریایی", "سولفات منیزیم",
            "سوپر فسفات", "میکرونوترینت", "آهن")

        val adapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            this, R.layout.custom_spinner_view, items)
        val fertilizerDropDownSpinner: Spinner = findViewById(R.id.fertilizerDropDownSpinner)
        fertilizerDropDownSpinner.setAdapter(adapter)
    }

    private fun getPlantsFromDatabase() {
        val db = DatabaseHelper(this)
        val plants = db.getPlants()
        db.close()
        plants_names = ArrayList<String>()
        for(plant in plants){
            plants_names!!.add(plant.plant_name)
        }
        val items = plants_names!!.toTypedArray()
        val adapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            this, R.layout.custom_spinner_view, items)

        namesDropDownSpinner?.setAdapter(adapter)
    }


    fun closeButtonClicked(view: View) {
        finish()
    }

    fun fertilizerBtnClicked(view: View) {
//        val plant_name = intent.getStringExtra("plantName")
//        val fertilizer_name = fertilizerDropDownSpinner.selectedItem.toString()
//        val days: Long = fertilizing_days.text.toString().toLong()
//        val data = Data.Builder()
//        data.putString("plant_name", plant_name)
//        data.putString("fertilizer_name", fertilizer_name)
//        data.putString("days", days.toString())
//        val constraints: Constraints = Constraints.Builder()
//            .build()
//        val myFirstWorkBuilder = OneTimeWorkRequest.Builder(FertilizerWorker::class.java)
//            .setConstraints(constraints)
//            .setInputData(data.build())
//            .build()
//        val workManager = WorkManager.getInstance(applicationContext)
//        workManager.enqueue(myFirstWorkBuilder)
//        updateFertilizerList()
//        Toast.makeText(applicationContext, "$fertilizer_name was successfully added.",
//            Toast.LENGTH_LONG).show()

    }

    fun wateringBtnClicked(view: View) {
        //val db = FertilizerDatabaseHelper(this)
//        val plant_name = intent.getStringExtra("plantName")
//        val days: Long = watering_days.text.toString().toLong()
//        val data = Data.Builder()
//        data.putString("plant_name", plant_name)
//        data.putString("days", days.toString())
//        val constraints: Constraints = Constraints.Builder()
//            .build()
//        val myFirstWorkBuilder = OneTimeWorkRequest.Builder(WaterWorker::class.java)
//            .setInitialDelay(days, TimeUnit.DAYS)
//            .setConstraints(constraints)
//            .setInputData(data.build())
//            .build()
//        val workManager = WorkManager.getInstance(applicationContext)
//        workManager.enqueue(myFirstWorkBuilder)
//        if(days != null){
//            currentWatering.text = days.toString() + " روز "
//        }else{
//            currentWatering.text = ""
//        }
    }

    fun removeFertilizerBtnClicked(view: View) {
//        val builder = AlertDialog.Builder(this)
//        builder.setMessage("Are you sure you want to Delete Fertilizer?")
//            .setCancelable(false)
//            .setPositiveButton("Yes") { dialog, id ->
//                val plant_name = intent.getStringExtra("plantName")
//                val db = FertilizerDatabaseHelper(this)
//                val fertilizer_name = fertilizer_list.adapter.
//                getItem(fertilizer_list.checkedItemPosition)
//                    .toString()
//                val fertilizer_id = db.getFertilizerId(plant_name, fertilizer_name)
//                val fertilizerID = UUID.fromString(fertilizer_id)
//                WorkManager.getInstance(applicationContext).cancelWorkById(fertilizerID)
//                db.removeFertilizer(plant_name, fertilizer_name)
//                db.close()
//                updateFertilizerList()
//            }
//            .setNegativeButton("No") { dialog, id ->
//                // Dismiss the dialog
//                dialog.dismiss()
//            }
//        val alert = builder.create()
//        alert.show()

    }

    private fun updateFertilizerList() {
        val db = DatabaseHelper(this)
        val fertilizers = db.getFertilizers(plants_names?.get(plant_position))
        val fertilizerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)

        if(fertilizers != null){
            for((fname, fdate) in fertilizers){

                Thread(Runnable {
                    runOnUiThread{
                        fertilizerAdapter.add(fname.toString() + " : " + fdate.toString())
                    }
                }).start()
            }
            fertilizer_list?.adapter = fertilizerAdapter
        }

        db.close()
    }

    private fun updateCurrentWatering() {
//        val db = FertilizerDatabaseHelper(this)
//        val water = db.getWatering(intent.getStringExtra("plantName"))
//        if(water.days != null){
//            currentWatering.text = water.days + " روز "
//        }else{
//            currentWatering.text = ""
//        }
//        db.close()
    }

    fun removeWateringBtnClicked(view: View) {
        // cancel watering by getting the watering id of the plant from database
//        val builder = AlertDialog.Builder(this)
//        builder.setMessage("Are you sure you want to Delete Watering?")
//            .setCancelable(false)
//            .setPositiveButton("Yes") { dialog, id ->
//                val plant_name = intent.getStringExtra("plantName")
//                val db = FertilizerDatabaseHelper(this)
//                val watering_id = db.getWateringId(plant_name)
//                val wateringID = UUID.fromString(watering_id)
//                WorkManager.getInstance(applicationContext).cancelWorkById(wateringID)
//                db.removeWatering(plant_name)
//                db.close()
//                updateCurrentWatering()
//            }
//            .setNegativeButton("No") { dialog, id ->
//                // Dismiss the dialog
//                dialog.dismiss()
//            }
//        val alert = builder.create()
//        alert.show()
    }


}
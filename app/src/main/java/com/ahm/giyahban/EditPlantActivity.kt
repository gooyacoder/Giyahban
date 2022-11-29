package com.ahm.giyahban


import android.os.Bundle
import android.view.View
import android.widget.AbsListView.CHOICE_MODE_SINGLE
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_edit_plant.*
import java.util.*
import java.util.concurrent.TimeUnit


class EditPlantActivity : AppCompatActivity() {

    var list_position = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_plant)
        val items = arrayOf( "20-20-20", "12-12-36", "10-52-10", "3-11-38",
            "هیومیک اسید", "قارچ کش", "اسید آمینه", "جلبک دریایی", "سولفات منیزیم",
            "سوپر فسفات", "میکرونوترینت", "آهن")

        val adapter: ArrayAdapter<Any?> = ArrayAdapter<Any?>(
            this, R.layout.custom_spinner_view, items)
        fertilizerDropDownSpinner.setAdapter(adapter)
        var plant_name = intent.getStringExtra("plantName")
        plantName.text = plant_name
        fertilizer_list.choiceMode = CHOICE_MODE_SINGLE
        fertilizer_list.setOnItemClickListener { parent, view, position, id ->
            list_position = position

        }

        updateFertilizerList()
        updateCurrentWatering()

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
//        var db = FertilizerDatabaseHelper(this)
//        var fertilizers = db.getFertilizers(intent.getStringExtra("plantName"))
//        val fertilizerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
//        for(fertilizer in fertilizers){
//
//            Thread(Runnable {
//                runOnUiThread{
//                    fertilizerAdapter.add(fertilizer.fertilizerName)
//                }
//            }).start()
//        }
//        fertilizer_list.adapter = fertilizerAdapter
//        db.close()
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
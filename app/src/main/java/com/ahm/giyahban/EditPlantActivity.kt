package com.ahm.giyahban


import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.View
import android.widget.*
import android.widget.AbsListView.CHOICE_MODE_SINGLE
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.collections.ArrayList


class EditPlantActivity : AppCompatActivity() {

    var list_position = 0
    var plant_position = 0
    var namesDropDownSpinner : Spinner? = null
    var plants_names: ArrayList<String>? = null
    var fertilizer_list: ListView? = null
    var fertilizerDropDownSpinner: Spinner? = null
    var fertilizing_days: EditText? = null
    var fertilizerDatesArrayList: ArrayList<String>? = null
    var fertilizerPeriodsArrayList: ArrayList<String>? = null
    var fertilizersArrayList: ArrayList<String>? = null

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
        fertilizerDropDownSpinner = findViewById(R.id.fertilizerDropDownSpinner)
        fertilizing_days = findViewById(R.id.fertilizing_days)
        fertilizerDatesArrayList = ArrayList()
        fertilizersArrayList = ArrayList()
        fertilizerPeriodsArrayList = ArrayList()
        namesDropDownSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                plant_position = position
                updateFertilizerList()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
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
            "Humic Acid", "Fungicide", "Amino Acid", "Sea Algae", "Magnesium Sulphate",
            "Super Phosphate", "Micronutrient", "Iron")

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
        val db = DatabaseHelper(this)
        val plant_name = plants_names?.get(plant_position)
        val fertilizer_name = fertilizerDropDownSpinner?.selectedItem.toString()
        fertilizersArrayList = db.getFertilizersNames(plant_name)
        fertilizerPeriodsArrayList = db.getFertilizerPeriodsArrayList(plant_name)
        fertilizersArrayList?.add(fertilizer_name)
        val days: String = fertilizing_days?.text.toString()
        fertilizerPeriodsArrayList?.add(days)
        val calendar = Calendar.getInstance()
        val today = calendar.time.time.toString()
        fertilizerDatesArrayList?.add(today)

        db.addFertilizer(plant_name, convertArrayToString(fertilizersArrayList),
            convertArrayToString(fertilizerDatesArrayList), convertArrayToString(fertilizerPeriodsArrayList))
        db.close()
        updateFertilizerList()
        val fertilizingEditText : EditText = findViewById(R.id.fertilizing_days)
        fertilizingEditText.setText("")
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
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to Delete Fertilizer?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                val plant_name = plants_names?.get(plant_position)
                val db = DatabaseHelper(this)
                val fertilizer_row = fertilizer_list?.adapter?.
                getItem(fertilizer_list!!.checkedItemPosition)
                    .toString()
                val fertilizer_name = ""
                val text = fertilizer_row.split(':')


                val fertilizersNamesArrayList = db.getFertilizersNames(plant_name)
                val index = fertilizersNamesArrayList?.indexOf(text[0].trim())
                fertilizersNamesArrayList?.remove(text[0].trim())
                val fertilizerPeriodsArrayList = db.getFertilizerPeriodsArrayList(plant_name)
                fertilizerPeriodsArrayList?.removeAt(index!!)
                db.removeFertilizer(plant_name, convertArrayToString(fertilizersNamesArrayList),
                                        convertArrayToString(fertilizerPeriodsArrayList))
                db.close()
                updateFertilizerList()
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()

    }

    private fun updateFertilizerList() {
        if(plants_names?.size!! > 0){
            val db = DatabaseHelper(this)
            val fertilizers = db.getFertilizers(plants_names?.get(plant_position))
            val fertilizerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)

            if(fertilizers != null){
                for((fn, fp) in fertilizers){
                    for(i in fn!!.indices){
                        Thread(Runnable {
                            runOnUiThread{
                                if(fn[i] != ""){
                                    fertilizerAdapter.add(fn[i] + " : " + fp?.get(i).toString())
                                }
                            }
                        }).start()
                    }
                }
                fertilizer_list?.adapter = fertilizerAdapter
            }

            db.close()
        }

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


    fun convertArrayToString(array: ArrayList<String>?): String? {
        val strSeparator = "__,__"
        var str = ""
        if(array != null){
            for (i in array.indices) {
                str = str + array[i]
                // Do not append comma at the end of last element
                if (i < array.size - 1) {
                    str = str + strSeparator
                }
            }
        }
        return str
    }

}
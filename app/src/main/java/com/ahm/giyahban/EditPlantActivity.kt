package com.ahm.giyahban


import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.AbsListView.CHOICE_MODE_SINGLE
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.collections.ArrayList
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString


class EditPlantActivity : AppCompatActivity() {

    var list_position = 0
    var plant_position = 0
    var namesDropDownSpinner : Spinner? = null
    var plants_names: ArrayList<String>? = null
    var fertilizer_list: ListView? = null
    var fertilizerDropDownSpinner: Spinner? = null
    var fertilizing_days: EditText? = null
    var fertilizersArrayList: ArrayList<Fertilizer>? = null
    var plantImageForEdit : ImageView? = null

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
        plantImageForEdit = findViewById(R.id.plantImageEdit)
        namesDropDownSpinner = findViewById(R.id.PlantsDropDownSpinner)
        fertilizerDropDownSpinner = findViewById(R.id.fertilizerDropDownSpinner)
        fertilizing_days = findViewById(R.id.fertilizing_days)
        fertilizersArrayList = ArrayList()
        namesDropDownSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                plant_position = position
                updateFertilizerList()
                updateCurrentWatering()
                loadPlantImage()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        fertilizer_list = findViewById(R.id.fertilizer_list)
    }

    private fun loadPlantImage() {
        val db = DatabaseHelper(this)
        if(plants_names != null && plants_names!!.size > 0){
            val plant_name = plants_names?.get(plant_position)
            if(plant_name != null){
                val plant_image = DbBitmapUtility.getImage(db.getPlant(plant_name).image)
                plantImageForEdit?.setImageBitmap(plant_image)

            }
        }
        db.close()
    }

    private fun prepareFertilizerList() {
        fertilizer_list?.choiceMode = CHOICE_MODE_SINGLE
        fertilizer_list?.setOnItemClickListener { parent, view, position, id ->
            list_position = position
        }

    }

    private fun prepareFertilizerDropDownSpinner() {
        val items = arrayOf( "20-20-20", "12-12-36", "10-52-10", "3-11-38",
            "Humic Acid", "Fungicide", "Amino Acid", "Seaweed", "Magnesium Sulphate",
            "Super Phosphate", "Micronutrient", "Iron", "Calcium Nitrate", "Urea",
            "Zinc Sulphate", "Sulfur", "Potassium Nitrate", "Potassium Sulphate")

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
        fertilizersArrayList = db.getFertilizersArrayList(plant_name)
        val days = fertilizing_days?.text.toString().toInt()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 5)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val today = calendar.time.time
        val newFertilizer = Fertilizer(fertilizer_name, today, days)
        fertilizersArrayList?.add(newFertilizer)
        val fert_string = Json.encodeToString(fertilizersArrayList)
        db.addFertilizer(plant_name, fert_string)
        db.close()
        updateFertilizerList()
        val fertilizingEditText : EditText = findViewById(R.id.fertilizing_days)
        fertilizingEditText.setText("")
    }

    fun wateringBtnClicked(view: View) {
        val db = DatabaseHelper(this)
        val plant_name = plants_names?.get(plant_position)
        val daysEditText: EditText = findViewById(R.id.watering_days)
        val days: String = daysEditText.text.toString()
        daysEditText.setText("")
        val currentWateringEditText: TextView = findViewById(R.id.currentWatering)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 5)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val date = calendar.time.time.toString()
        db.addWatering(plant_name, date, days)
        if(days != null){
            currentWateringEditText.text = days + " روز "
        }else{
            currentWateringEditText.text = ""
        }
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

                val text = fertilizer_row.split(':')
                val fertilizers: ArrayList<Fertilizer>? = db.getFertilizersArrayList(plant_name)
                var fertilizer: Fertilizer? = null
                if (fertilizers != null) {
                    for(fert in fertilizers){
                        if(fert.name == text[0].trim())
                            fertilizer = fert
                    }
                }
                fertilizers?.remove(fertilizer)
                val fert_string = Json.encodeToString(fertilizers)
                db.removeFertilizer(plant_name, fert_string)
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
                                fertilizerAdapter.add(fn[i] + " : " + fp?.get(i).toString())
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
        val db = DatabaseHelper(this)
        if(plants_names != null && plants_names!!.size > 0){
            val plant_name = plants_names?.get(plant_position)
            if(plant_name != null){
                val wateringDays = db.getWateringDays(plant_name)
                val CurrentWatering: TextView = findViewById(R.id.currentWatering)
                if(wateringDays != "null"){
                    CurrentWatering.text = wateringDays + " روز "
                }
                else{
                    CurrentWatering.text = ""
                }

            }
        }
        db.close()
    }

    fun removeWateringBtnClicked(view: View) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to Delete Watering?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, id ->
                val db = DatabaseHelper(this)
                val plant_name = plants_names?.get(plant_position)
                db.removeWatering(plant_name)
                db.close()
                updateCurrentWatering()
            }
            .setNegativeButton("No") { dialog, id ->
                // Dismiss the dialog
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

}
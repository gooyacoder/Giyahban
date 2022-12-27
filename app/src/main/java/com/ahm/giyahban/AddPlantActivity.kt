package com.ahm.giyahban

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity



class AddPlantActivity : AppCompatActivity() {

    var imageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_plant)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            imageBitmap = data?.extras?.get("data") as Bitmap
            val plant_preview: ImageView = findViewById(R.id.plant_preview)
            plant_preview.setImageBitmap(imageBitmap)
        }
    }

    fun pictureBtnClicked(view: View) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, 1)
    }

    fun addBtnClicked(view: View) {
        val name_text: EditText = findViewById(R.id.name_text)
        if(name_text.text.toString() != ""){
            if(imageBitmap == null){
                imageBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            }
            var image_byte_array = imageBitmap?.let { DbBitmapUtility.getBytes(it) }
            var db = DatabaseHelper(this)

            var plant_name:String = name_text.text.toString()
            val result = db.addEntry(plant_name, image_byte_array!!)
            if(result.toInt() == -1){
                Toast.makeText(this, "Try another plant name.", Toast.LENGTH_LONG).show()
            }else{
                db.close()
                Toast.makeText(this, "$plant_name added to database, successfully.", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }
}
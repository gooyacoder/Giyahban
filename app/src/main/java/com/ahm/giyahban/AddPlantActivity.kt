package com.ahm.giyahban

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
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

    /*fun addBtnClicked(view: View) {
        if(imageBitmap == null){
            imageBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        }
        var image_byte_array = imageBitmap?.let { DbBitmapUtility.getBytes(it) }
        var db = DatabaseHelper(this)
        var plant_name:String? = name_text.text.toString()
        db.addEntry(plant_name, image_byte_array)
        db.close()
        finish()
    }*/
}
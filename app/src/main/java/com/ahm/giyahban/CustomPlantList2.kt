package com.ahm.giyahban

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView



class CustomPlantList2(
    private val context: Activity,
    private val plantNames: MutableList<String>,
    private val plantImages: MutableList<Bitmap>) :
    ArrayAdapter<String>(context, R.layout.row_item, plantNames) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        val inflater = context.layoutInflater
        if (convertView == null) row = inflater.inflate(R.layout.row_item, null, true)
        val textViewPlantName = row!!.findViewById<View>(R.id.textViewPlantName) as TextView
        val plantImage = row.findViewById<View>(R.id.imageViewPlant) as ImageView
        val deleteButton = row.findViewById<View>(R.id.delete_button) as Button
        deleteButton.setFocusable(false)
        textViewPlantName.text = plantNames[position]
        plantImage.setImageBitmap(plantImages[position])
        return row
    }
}

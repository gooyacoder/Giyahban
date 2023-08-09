package com.ahm.giyahban

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class TaskChooserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tast_chooser)
    }

    fun watering_btn_clicked(view: View) {
        val intent = Intent(this, TodaysWateringsActivity::class.java)
        startActivity(intent)
    }

    fun fertilizer_btn_clicked(view: View) {
        val intent = Intent(this, TodaysFertilizersActivity::class.java)
        startActivity(intent)
    }
}
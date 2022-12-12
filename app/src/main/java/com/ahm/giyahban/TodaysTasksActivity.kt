package com.ahm.giyahban

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class TodaysTasksActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_todays_tasks)
        prepareUI()
        getTodaysTasks()

    }

    private fun getTodaysTasks() {
        TODO("Not yet implemented")
    }

    private fun prepareUI() {
        TODO("Not yet implemented")
    }
}
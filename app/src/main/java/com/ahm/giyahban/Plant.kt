package com.ahm.giyahban

import java.util.ArrayList

data class Plant(val plant_name: String,
                 val image: ByteArray,
                 val water_date: Long?,
                 val water_period: Int?,
                 val fertilizers: ArrayList<String>?,
                 val fertilizer_dates: ArrayList<String>?,
                 val fertilizer_periods: ArrayList<String>? )  {}
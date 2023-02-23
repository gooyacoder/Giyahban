package com.ahm.giyahban

import java.util.ArrayList
import kotlinx.serialization.*
import kotlinx.serialization.json.*
@Serializable
data class Plant(val plant_name: String,
                 val image: ByteArray,
                 val water_date: Long?,
                 val water_period: Int?,
                 val fertilizers: ArrayList<Fertilizer>?)
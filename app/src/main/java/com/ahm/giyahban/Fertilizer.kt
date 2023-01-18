package com.ahm.giyahban

import java.util.ArrayList
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Fertilizer(val name: String,
                      val date: Long,
                      val period: Int ){}

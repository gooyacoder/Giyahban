package com.ahm.giyahban

import java.util.ArrayList
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Fertilizer(val name: String,
                      var date: Long,
                      val period: Int ){}

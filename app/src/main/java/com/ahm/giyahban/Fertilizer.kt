package com.ahm.giyahban

import kotlinx.serialization.*

@Serializable
data class Fertilizer(val name: String, var date: Long, val period: Int ){}

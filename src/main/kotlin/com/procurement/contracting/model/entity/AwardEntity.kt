package com.procurement.contracting.model.entity

import java.util.*


data class AwardEntity(

        val cpId: String,

        val acId: String,

        val token: UUID,

        val owner: String,

        var jsonData: String
)

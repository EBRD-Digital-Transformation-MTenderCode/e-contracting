package com.procurement.contracting.model.entity

import java.util.*

data class AcEntity(

        val cpId: String,

        val stage: String,

        val token: UUID,

        val owner: String,

        val createdDate: Date,

        val canId: String,

        val status: String,

        val statusDetails: String,

        val jsonData: String
)

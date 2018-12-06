package com.procurement.contracting.model.entity

import java.util.*

data class AcEntity(

        val cpId: String,

        var acId: String,

        val token: UUID,

        val owner: String,

        val createdDate: Date,

        val canId: String,

        val status: String,

        var statusDetails: String,

        val mainProcurementCategory: String,

        val language: String,

        var jsonData: String
)
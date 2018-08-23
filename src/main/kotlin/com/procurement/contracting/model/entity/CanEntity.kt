package com.procurement.contracting.model.entity

import java.util.*

data class CanEntity(

        val cpId: String,

        val stage: String,

        val canId: UUID,

        val owner: String,

        val createdDate: Date,

        val awardId: String,

        var acId: String?,

        var status: String,

        var statusDetails: String
)

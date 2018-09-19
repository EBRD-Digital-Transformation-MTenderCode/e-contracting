package com.procurement.contracting.model.entity

import java.util.*

data class HistoryEntity(

        var operationId: String,

        var command: String,

        var operationDate: Date,

        var jsonData: String
)



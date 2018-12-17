package com.procurement.contracting.model.dto.ocds

import java.time.LocalDateTime

data class TreasuryData(

        val id_dok: String,

        val id_hist: String,

        val status: String,

        val st_date: LocalDateTime,

        val reg_nom: Long,

        val reg_date: LocalDateTime,

        val descr: String
)
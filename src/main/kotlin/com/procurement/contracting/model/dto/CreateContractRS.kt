package com.procurement.contracting.model.dto

import com.procurement.contracting.model.dto.ocds.Can
import com.procurement.contracting.model.dto.ocds.Contract

data class CreateContractRS(

        val cans: List<Can>?,

        val contracts: List<Contract>?
)

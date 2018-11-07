package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Can @JsonCreator constructor(

        val contract: Contract
)

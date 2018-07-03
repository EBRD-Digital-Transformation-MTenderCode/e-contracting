package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrganizationReference @JsonCreator constructor(

        @field:NotNull
        val id: String,

        @field:NotNull
        val name: String
)
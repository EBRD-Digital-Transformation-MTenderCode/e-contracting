package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrganizationReference(

        @JsonProperty("id") @NotNull
        val id: String,

        @JsonProperty("name") @field:Size(min = 1) @NotNull
        val name: String
)
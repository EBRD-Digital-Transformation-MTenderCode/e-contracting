package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import javax.validation.Valid
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Item(

        @JsonProperty("id") @NotNull
        val id: String,

        @JsonProperty("description") @NotNull
        val description: String,

        @JsonProperty("classification") @Valid @NotNull
        val classification: Classification,

        @JsonProperty("additionalClassifications") @Valid
        val additionalClassifications: Set<Classification>?,

        @JsonProperty("quantity") @NotNull
        val quantity: BigDecimal,

        @JsonProperty("unit") @Valid @NotNull
        val unit: Unit,

        @JsonProperty("relatedLot") @NotNull
        val relatedLot: String
)
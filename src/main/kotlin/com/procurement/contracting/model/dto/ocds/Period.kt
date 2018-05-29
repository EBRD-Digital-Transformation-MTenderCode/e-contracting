package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.contracting.model.dto.databinding.JsonDateDeserializer
import com.procurement.contracting.model.dto.databinding.JsonDateSerializer
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

data class Period(

        @JsonProperty("startDate") @NotNull
        @JsonDeserialize(using = JsonDateDeserializer::class)
        @JsonSerialize(using = JsonDateSerializer::class)
        val startDate: LocalDateTime,

        @JsonProperty("endDate") @NotNull
        @JsonDeserialize(using = JsonDateDeserializer::class)
        @JsonSerialize(using = JsonDateSerializer::class)
        val endDate: LocalDateTime
)
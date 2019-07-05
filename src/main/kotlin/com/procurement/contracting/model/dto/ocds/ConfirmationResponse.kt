package com.procurement.contracting.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.contracting.domain.model.confirmation.response.ConfirmationResponseType
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ConfirmationResponse @JsonCreator constructor(

        val id: String,

        val value: ConfirmationResponseValue,

        val request: String
)

data class ConfirmationResponseValue @JsonCreator constructor(

        val name: String,

        val id: String,

        val date: LocalDateTime,

        val relatedPerson: RelatedPerson?,

        val verification: List<Verification>
)

data class Verification @JsonCreator constructor(

        val type: ConfirmationResponseType,

        val value: String,

        val rationale: String
)

package com.procurement.contracting.application.service.rule.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.contracting.domain.model.confirmation.request.ConfirmationRequestSource

class SourceOfConfirmationRequestRule(
    @field:JsonProperty("role") @param:JsonProperty("role") val role: ConfirmationRequestSource
)

package com.procurement.contracting.application.service.rule.model

import com.fasterxml.jackson.annotation.JsonProperty

class StateForSettingRule(
    @field:JsonProperty("status") @param:JsonProperty("status") val status: String,
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: String
)

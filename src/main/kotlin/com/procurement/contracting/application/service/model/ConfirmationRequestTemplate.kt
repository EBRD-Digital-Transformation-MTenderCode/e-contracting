package com.procurement.contracting.application.service.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude


@JsonInclude(JsonInclude.Include.NON_NULL)
data class ConfirmationRequestTemplate @JsonCreator constructor(

        var id: String,

        var type: String,

        var title: String,

        var description: String,

        var relatesTo: String,

        var requestTitle: String,

        var requestDescription: String
)